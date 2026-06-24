package ding.co.hellojpa.week3;

import ding.co.hellojpa.UniMember;
import ding.co.hellojpa.UniTeam;
import jakarta.persistence.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Week3LoadingTest {

    @Autowired
    EntityManagerFactory emf;

    @Test
    void 지연로딩_LAZY_동작_확인() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // [준비] 데이터 저장
            // (UniTeam, UniMember 엔티티는 @ManyToOne(fetch = LAZY)로 설정되어 있음)
            UniTeam team = new UniTeam();
            team.setName("TeamA");
            em.persist(team);
            UniMember member = new UniMember();
            member.setTeam(team);
            em.persist(member);

            em.flush();
            em.clear(); // 영속성 컨텍스트 비우기

            System.out.println("=== 1. Member 조회 시작 ===");
            UniMember findMember = em.find(UniMember.class, member.getId());
            System.out.println("=== Member 조회 끝 ===");

            // 로그 확인: SELECT ... FROM UniMember ... (Team 조인 없음!)
            // Team 자리에는 '프록시(가짜 객체)'가 들어있음

            System.out.println("=== 2. Team 이름 사용 (초기화) ===");
            // 이때! Team을 조회하는 쿼리가 나갑니다!
            System.out.println("팀 이름 = " + findMember.getTeam().getName());

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}