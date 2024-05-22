package com.project2spring.service.member;

import com.project2spring.domain.member.Member;
import com.project2spring.mapper.member.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class MemberService {
    private final MemberMapper mapper;

    public void add(Member member) {
        mapper.insert(member);
    }
}
