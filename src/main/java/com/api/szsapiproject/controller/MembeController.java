package com.api.szsapiproject.controller;

import com.api.szsapiproject.entity.Member;
import com.api.szsapiproject.repository.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/szs")
//@RequiredArgsConstructor
@RestController
public class MembeController {
//    @Autowired
//    MemberRepository memberRepository;

    private final MemberRepository memberRepository;

    public MembeController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @PostMapping("/singup")
    public ResponseEntity saveMember(@RequestBody Member member) {
        System.out.println(">>>>> getName: " + member.getName());

        Member resMember = memberRepository.save(member);

        if (resMember != null) { // 회원가입 성공
            return new ResponseEntity(resMember, HttpStatus.OK);
        } else { // 회원가입 실패
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/login")
    public ResponseEntity loginMember(@RequestBody Member member) {
        Member resMember = memberRepository.findByUserIdAndPassword(member.getUserId(), member.getPassword());

        if (resMember != null) { // 로그인 성공
            return new ResponseEntity(resMember, HttpStatus.OK);
        } else { // 로그인 실패
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
    }

}
