package ding.co.hellojpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberJpaRepository memberJpaRepository;

    public void join(Member member) {
        memberRepository.save(member);
        memberJpaRepository.save(member);
    }
}
