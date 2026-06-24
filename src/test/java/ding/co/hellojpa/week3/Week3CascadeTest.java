package ding.co.hellojpa.week3;

import jakarta.persistence.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Week3CascadeTest {

    @Autowired EntityManagerFactory emf;

    @Test
    void 영속성전이_저장_테스트() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 1. 자식 객체 2개 생성 (아직 영속 상태 아님)
            CascadeChild child1 = new CascadeChild();
            child1.setName("첫째");

            CascadeChild child2 = new CascadeChild();
            child2.setName("둘째");

            // 2. 부모 객체 생성 (아직 영속 상태 아님)
            CascadeParent parent = new CascadeParent();
            parent.setName("아빠");

            // 3. 연관관계 맺기 (편의 메서드 활용)
            parent.addChild(child1);
            parent.addChild(child2);

            System.out.println("========= 저장 시작 =========");

            // 4. 저장 (CASCADE의 마법)
            // ⭐️ 주의: 자식들은 persist 하지 않고, 오직 부모만 persist 합니다!
            em.persist(parent);

            System.out.println("========= 저장 끝 =========");

            // 강제로 DB에 쿼리 전송
            em.flush();

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}