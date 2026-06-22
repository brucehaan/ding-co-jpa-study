package ding.co.hellojpa;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Week2SpringDataJpaTest {

    // 우리가 만든 건 인터페이스 뿐이지만, 스프링이 구현체를 주입해 줍니다.
    @Autowired
    MemberRepository memberRepository;

    // 추후 테스트용도임
    @Autowired
    EntityManager em;

    @Test
    void 스프링데이터JPA_구현체_확인() {
        System.out.println("========================================");
        System.out.println("1. 주입된 객체의 진짜 클래스 이름 확인");
        System.out.println("클래스: " + memberRepository.getClass().getName());

        System.out.println("\n2. 이 객체가 구현하고 있는 인터페이스 목록");
        for (Class<?> iface : memberRepository.getClass().getInterfaces()) {
            System.out.println("- " + iface.getName());
        }
        System.out.println("========================================");
    }

    @Test
    @Transactional
    void JpaRepository로_저장해도_EntityManager가_관리한다() {
        System.out.println("=== 1. JpaRepository로 엔티티 저장 ===");
        Member member = new Member();
        member.setName("스프링데이터JPA_회원");

        // JpaRepository를 사용해서 저장!
        Member savedMember = memberRepository.save(member);

        System.out.println("\n=== 2. EntityManager가 이 녀석을 알고 있을까? (contains) ===");
        // em.contains(): 영속성 컨텍스트(1차 캐시)가 이 객체를 관리하고 있는지 확인하는 메서드
        boolean isManaged = em.contains(savedMember);
        System.out.println("EntityManager가 관리 중인가? " + isManaged);

        System.out.println("\n=== 3. EntityManager로 똑같은 ID를 조회해보기 (1차 캐시 확인) ===");
        // DB에 SELECT 쿼리가 나갈까? 안 나갈까?
        Member emFoundMember = em.find(Member.class, savedMember.getId());

        System.out.println("두 객체의 주소값이 같은가? " + (savedMember == emFoundMember));

        // [검증]
        // 1. isManaged는 true여야 합니다. (관리를 받고 있음)
        // 2. 두 객체는 완벽히 동일해야 합니다 (1차 캐시에서 꺼내왔으므로 SELECT 쿼리도 안 나감)
    }

    @Test
    @Transactional
    void JpaRepository로_조회해도_변경감지_더티체킹이_동작한다() {
        // [준비] DB에 데이터 하나 넣고 영속성 컨텍스트 비우기
        Member member = new Member();
        member.setName("원본_이름");
        memberRepository.save(member);

        em.flush();
        em.clear(); // 비서 퇴근! (이제 DB에서 새로 가져와야 함)

        System.out.println("=== 1. JpaRepository로 데이터 조회 ===");
        // JpaRepository를 통해 조회합니다. (이 순간 다시 영속 상태가 됨)
        Member findMember = memberRepository.findById(member.getId()).orElseThrow();

        System.out.println("\n=== 2. 값만 수정하고 save()는 호출 안 함! ===");
        findMember.setName("수정된_이름");

        System.out.println("\n=== 3. 플러시(또는 커밋) 발생 시 UPDATE 쿼리가 나갈까? ===");
        // 트랜잭션 종료 시점에 알아서 나가지만, 눈으로 바로 확인하기 위해 강제 플러시
        em.flush();

        // [결과 확인]
        // 콘솔 창에 UPDATE 쿼리가 찍혔다면 성공!
        // JpaRepository로 가져온 객체도 영속성 컨텍스트의 '변경 감지(Dirty Checking)' 기능을 100% 누린다는 증거입니다.
    }
}