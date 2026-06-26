package ding.co.hellojpa.week3;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;

@Slf4j
@SpringBootTest
public class Week3ProxyTest {

    @Autowired
    EntityManagerFactory emf;

    @Test
    void 프록시_가짜객체_확인_테스트() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 1. Member 저장
            UniMember member = new UniMember();
            member.setUsername("JPA_PROXY");
            em.persist(member);

            em.flush();
            em.clear(); // 영속성 컨텍스트 비우기

            log.info("1. getReference() 호출");
            UniMember findMember = em.getReference(UniMember.class, member.getId());
            log.info("2. 클래스 타입 확인");
            log.info("findMember 클래스 = {}", findMember.getClass().getName());
            // 예상 출력: ding.co.hellojpa.UniMember$HibernateProxy$xyz123 (가짜 객체!)

            log.info("3. 실제 데이터 요청 (이 순간 쿼리 발생) ");
            log.info("member 이름 = {}", findMember.getUsername());

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
