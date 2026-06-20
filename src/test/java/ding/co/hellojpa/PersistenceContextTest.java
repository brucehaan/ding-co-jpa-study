package ding.co.hellojpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest // 스프링 부트의 모든 설정을 가져와서 테스트
@Slf4j
public class PersistenceContextTest {
    @Autowired
    EntityManagerFactory emf; // 공장을 주입받는다.

    @Test
    void 영속성_컨텍스트_생존확인() {
        // 1. 비서(EntityManager) 생성
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        // 2. 업무 시작
        tx.begin();

        try {
            // [준비] DB에 데이터가 없으니 1번 유저를 미리 저장해둡니다.
            User initialUser = new User();
            // initialUser.setId(1L); // IDENTITY 전략이면 생략 가능
            initialUser.setName("UserA");
            em.persist(initialUser);

            // 영속성 컨텍스트를 비우고(DB에 반영하고) 초기화 (실험을 위해 깨끗하게!)
            em.flush();
            em.clear();

            log.info("=== 🚀 실험 시작 ===");

            // 3. 비서에게 1번 회원 조회를 시킴
            // (DB에 갔다가 캐시에 저장함)
            User user = em.find(User.class, 1L);
            log.info("-> 1번 조회 완료 (DB 쿼리 나감)");

            // 4. 이름 변경 (비서에게 "이름 바꿔"라고 말만 함)
            user.setName("JPA Master");
            log.info("-> setName 호출 완료 (쿼리 안 나감!)");
            // -> 중요! 여기서 UPDATE 쿼리가 나가지 않습니다!

            log.info("=== 🛑 커밋 직전 ===");

            // 5. 업무 종료 (커밋)
            tx.commit();
            log.info("-> 커밋 완료 (이때 UPDATE 쿼리 실행)");

        } catch (Exception e) {
            tx.rollback(); // 에러 나면 취소
            e.printStackTrace();
        } finally {
            em.close(); // 비서 퇴근 (중요!)
        }
    }
}
