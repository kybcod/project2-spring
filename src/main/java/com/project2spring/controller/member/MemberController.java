package com.project2spring.controller.member;

import com.project2spring.domain.Member;
import com.project2spring.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService service;

    @PostMapping("join")
    public void join(@RequestBody Member member) {
        service.add(member);
    }

    @GetMapping(value = "check", params = "email")
    public ResponseEntity checkEmail(String email) {
        Member member = service.getByEmail(email.trim());
        if (member == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().build();
        }
    }

    @GetMapping(value = "check", params = "nickName")
    public ResponseEntity checkNickName(String nickName) {
        Member member = service.getByNickName(nickName.trim());
        if (member == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().build();
        }
    }
}
