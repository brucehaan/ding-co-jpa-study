package ding.co.hellojpa.week3;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 읽기 전용 트랜잭션 (성능 최적화)
public class UniMemberService {
    private final UniMemberRepository memberRepository;

    // bad 그냥 조회 (프록시 상태로 반환) -> 컨트롤러에서 폭탄 터짐
    public UniMember getMemberUni(Long id) {
        return memberRepository.findById(id).orElseThrow();
    }

    // good 1. 패치 조인 사용(진짜 객체로 반환) -> 안전함
    public UniMember getMemberFetchJoin(Long id) {
        return memberRepository.findByIdFetchJoin(id).orElseThrow();
    }

    // good2. DTO 사용 (데이터만 반환) -> 안전함
    public MemberTeamDto getMemberDto(Long id) {
        return memberRepository.findDtoById(id).orElseThrow();
    }

}
