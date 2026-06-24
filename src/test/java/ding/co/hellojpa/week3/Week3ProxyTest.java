package ding.co.hellojpa.week3;

import ding.co.hellojpa.UniMember;
import jakarta.persistence.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Week3ProxyTest {

    @Autowired EntityManagerFactory emf;

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

            System.out.println("=== 1. getReference() 호출 (DB 조회 안 함) ===");
            UniMember findMember = em.getReference(UniMember.class, member.getId());

            System.out.println("=== 2. 클래스 타입 확인 ===");
            System.out.println("findMember 클래스 = " + findMember.getClass().getName());
            // 예상 출력: ding.co.hellojpa.UniMember$HibernateProxy$xyz123 (가짜 객체!)

            System.out.println("=== 3. 실제 데이터 요청 (이 순간 쿼리 발생!) ===");
            System.out.println("member 이름 = " + findMember.getUsername());

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}