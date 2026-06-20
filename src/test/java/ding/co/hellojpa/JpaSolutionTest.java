package ding.co.hellojpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JpaSolutionTest {

    @Autowired
    EntityManagerFactory emf;

    @Test
    void JPA_동일성_보장_확인() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // [준비] 데이터 저장
            Member initialMember = new Member("UserA", Grade.BASIC);
            em.persist(initialMember);

            em.flush();
            em.clear(); // 영속성 컨텍스트 초기화 (실험 시작)

            System.out.println("=== ❤️ JPA 실험 시작 ===");

            // [A] 1. 서비스 로직 시작: 회원 조회
            // DB에서 가져와서 1차 캐시에 저장하고, 그 주소값(@100)을 줌
            Member member = em.find(Member.class, initialMember.getId());
            System.out.println("1. 처음 조회된 등급: " + member.getGrade()); // BASIC
            System.out.println("-> 참조값: " + member);

            // [B] 2. 등급 상향 메서드 호출 (ID만 넘김, em도 같이 넘김)
            promoteToVip(em, initialMember.getId());

            // [C] 3. 등급 확인
            // promoteToVip 안에서 바꾼 내용이 여기서도 보일까?
            System.out.println("3. 메인 로직에서 다시 확인한 등급: " + member.getGrade());
            System.out.println("-> 참조값: " + member); // 아까랑 똑같은 주소!

            // [결과 검증]
            if (member.getGrade() == Grade.VIP) {
                System.out.println(">>> 🎉 VIP 혜택 적용 성공! (우리는 하나다)");
            } else {
                System.out.println(">>> 😭 혜택 적용 실패...");
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // [D] 별도 서비스 역할 (JPA 사용)
    private void promoteToVip(EntityManager em, Long memberId) {
        // 여기서 find를 호출해도 DB에 안 감! (1차 캐시 사용)
        Member member = em.find(Member.class, memberId);

        // 등급 변경 (Dirty Checking)
        member.setGrade(Grade.VIP);

        System.out.println("2. [DiscountService] 등급 변경 완료! (BASIC -> VIP)");
    }
}