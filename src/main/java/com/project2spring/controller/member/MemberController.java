package com.project2spring.controller.member;

import com.project2spring.domain.Member;
import com.project2spring.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService service;

    @PostMapping("join")
    public ResponseEntity join(@RequestBody Member member) {
        if (service.validate(member)) {
            service.add(member);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
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

    @GetMapping("list")
    public List<Member> list() {
        return service.list();
    }

    @GetMapping("{id}")
    public ResponseEntity get(@PathVariable Integer id) {
        Member member = service.getById(id);
        if (member == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(member);
    }

    @DeleteMapping("{id}")
    public ResponseEntity delete(@RequestBody Member member) {
        if (service.hasAccess(member)) {
            service.delete(member.getId());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
