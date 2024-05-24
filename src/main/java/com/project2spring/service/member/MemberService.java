package com.project2spring.service.member;

import com.project2spring.domain.Member;
import com.project2spring.mapper.member.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class MemberService {
    private final MemberMapper mapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public void add(Member member) {
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        member.setEmail(member.getEmail().trim());
        member.setEmail(member.getNickName().trim());
        mapper.insert(member);
    }

    public boolean validate(Member member) {
        if (member.getNickName() == null || member.getNickName().isBlank()) {
            return false;
        }
        if (member.getPassword() == null || member.getPassword().isBlank()) {
            return false;
        }
        if (member.getEmail() == null || member.getEmail().isBlank()) {
            return false;
        }

        String emailPatter = "[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*";
        if (!member.getEmail().matches(emailPatter)) {
            return false;
        }
        return true;
    }

    public Member getByEmail(String email) {
        return mapper.selectByEmail(email);
    }

    public Member getByNickName(String nickName) {
        return mapper.selectByNicKName(nickName);
    }


    public List<Member> list() {
        return mapper.selectAll();
    }

    public Member getById(Integer id) {
        return mapper.selectById(id);
    }

    public void delete(Integer id) {
        mapper.deleteById(id);
    }

    public boolean hasAccess(Member member) {
        Member db = mapper.selectById(member.getId());
        if (db == null) {
            return false;
        }
        return passwordEncoder.matches(member.getPassword(), db.getPassword());
    }

    public boolean hasAccessModify(Member member) {
        Member dbmember = mapper.selectById(member.getId());
        if (dbmember == null) {
            return false;
        }

        if (!passwordEncoder.matches(member.getPassword(), dbmember.getPassword())) {
            return false;
        }
        return true;
    }

    public void modify(Member member) {
        if (member.getPassword() != null && member.getPassword().trim().length() > 0) {
            member.setPassword(passwordEncoder.encode(member.getPassword()));
        } else {
            Member db = mapper.selectById(member.getId());
            member.setPassword(db.getPassword());
        }
        mapper.update(member);
    }
}
