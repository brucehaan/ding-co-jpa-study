package ding.co.hellojpa.week4;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class Week4EmbeddedTest {
    @Autowired
    EntityManagerFactory emf;

    @Test
    void 임베디드타입_저장및재정의_테스트() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            log.info("1. 객체지향적인 데이터 세팅");
            OverrideMember member = new OverrideMember();

            // 값 타입 활용 1 : 집 주소 세팅
            Address home = new Address("서울", "강남대로", "12345");
            member.setHomeAddress(home);

            // 값 타입 활용 2 : 회사 주소 세팅 (Override 대상)
            Address work = new Address("판교", "테헤란로", "67890");
            member.setWorkAddress(work);

            em.persist(member);

            // 쿼리 확인을 위해 flush
            em.flush();
            em.clear();

            log.info("2. 데이터 조회 및 확인");
            OverrideMember findMember = em.find(OverrideMember.class, member.getId());

            log.info("집 도시 : {}", findMember.getHomeAddress().getCity());
            log.info("회사 도시 : {}", findMember.getWorkAddress().getCity());

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
    }
}
