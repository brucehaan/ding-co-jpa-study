package ding.co.hellojpa.week3;

import lombok.RequiredArgsConstructor;
import org.hibernate.LazyInitializationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UniMemberController {
    private final UniMemberService memberService;

    // 1. 실패 : Uni 로딩 에러 재현 API
    // URL: http://localhost:8080/lazy/error/1
    @GetMapping("/lazy/error/{id}")
    public String getTeamNameError(@PathVariable Long id) {
        // 1. 서비스 호출(트랜잭션 시작 -> 종료)
        // 반환된 member는 '준영속' 상태. Team은 프록시(가짜)
        UniMember member = memberService.getMemberUni(id);

        try {
            // 2. 프록시 초기화 시도
            // 준영속 상태인데 db에 또 가려고 하네? -> 에러 발생
            String teamName = member.getTeam().getName();
            return "팀 이름 : " + teamName;
        } catch (LazyInitializationException e) {
            e.printStackTrace();
            return "에러 발생 : LazyInitializationException (콘솔 로그 확인)";
        }
    }

    // 2. 성공 : fetch Join 사용 API
    // URL: http://localhost:8080/lazy/fetch/1
    @GetMapping("/lazy/fetch/{id}")
    public String getTeamNameFetch(@PathVariable Long id) {
        // 서비스에서 이미 teamRkwl Join해서 가져왔음 (진짜 객체)
        UniMember member = memberService.getMemberFetchJoin(id);

        // db 조회 없이 메모리에 있는 값 바로 꺼냄 -> 성공
        return "팀 이름(Fetch Join) : " + member.getTeam().getName();
    }

    // 3. 성공: DTO 직접 조회 API
    // URL: http://localhost:8080/lazy/dto/1
    @GetMapping("/lazy/dto/{id}")
    public MemberTeamDto getMemberDto(@PathVariable Long id) {
        // 엔티티가 아니라 순수 데이터 객체(DTO)를 받음
        // LAZY고 뭐고 신경 쓸 필요 없음
        return memberService.getMemberDto(id);
    }


}
