package ding.co.hellojpa;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class Week2SaveTrapTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Test
    void Save_반환값_무시_함정_테스트() {
        // 1. 기존 데이터 하나 저장 (ID 1L 생성)
        Member original = new Member("기존이름", Grade.BASIC);
        memberRepository.save(original);

        em.flush();
        em.clear(); // 영속성 컨텍스트 비우기

        System.out.println("\n=== 2. 화면에서 데이터가 넘어왔다고 가정 (준영속) ===");
        Member detachedMember = new Member("바꿀이름", Grade.BASIC);
        detachedMember.setId(original.getId()); // 기존 ID 세팅 (isNew == false)

        System.out.println("\n=== 3. save() 호출 ===");
        // ID가 있으므로 merge()가 발동됨
        Member managedMember = memberRepository.save(detachedMember);

        System.out.println("원본 객체(detached) 주소: " + System.identityHashCode(detachedMember));
        System.out.println("반환 객체(managed) 주소: " + System.identityHashCode(managedMember));

        if (detachedMember != managedMember) {
            System.out.println(">>> 🚨 경고: 두 객체는 완전히 다른 인스턴스입니다!");
        }

        System.out.println("\n=== 4. 추가 수정 시도 ===");
        // ❌ 실수로 원본 객체의 값을 바꿈
        detachedMember.setName("버그발생_이름");

        // ✅ 정상적으로 반환 객체의 값을 바꿈
        managedMember.setName("정상반영_이름");

        em.flush(); // 강제 쿼리 전송

        // 5. DB에서 실제 적용된 이름 확인
        em.clear();
        Member result = memberRepository.findById(original.getId()).orElseThrow();
        System.out.println("최종 DB에 저장된 이름: " + result.getName());

        // 최종 이름은 "정상반영_이름" 이어야 합니다. "버그발생_이름"은 무시됨.
    }
}