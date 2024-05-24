package com.project2spring.service.member;

import com.project2spring.domain.Member;
import com.project2spring.mapper.member.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class MemberService {
    private final MemberMapper mapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

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

    public Map<String, Object> getToken(Member member) {

        Map<String, Object> result = null;

        Member db = mapper.selectByEmail(member.getEmail());

        if (db != null) {
            if (passwordEncoder.matches(member.getPassword(), db.getPassword())) {
                result = new HashMap<>();
                String token = "";

                // 토큰 만드는 코드
                JwtClaimsSet claims = JwtClaimsSet.builder()
                        .issuer("self")
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(60 * 60 * 24 * 7)) //일주일
                        .subject(db.getId().toString())
                        .claim("scope", "") //권한
                        .claim("nickName", db.getNickName())
                        .build();
                token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
                result.put("token", token);
            }
        }
        return result;
    }
}
