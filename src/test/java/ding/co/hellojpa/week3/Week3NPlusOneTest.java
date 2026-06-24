package ding.co.hellojpa.week3;

import ding.co.hellojpa.UniMember;
import ding.co.hellojpa.UniTeam;
import jakarta.persistence.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

@SpringBootTest
public class Week3NPlusOneTest {

    @Autowired EntityManagerFactory emf;

    @Test
    void N플러스1_문제_발생과_해결_테스트() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // ==========================================
            // [데이터 준비] 팀 3개, 각 팀에 회원 1명씩 저장
            // ==========================================
            for (int i = 1; i <= 3; i++) {
                UniTeam team = new UniTeam();
                team.setName("Team" + i);
                em.persist(team);

                UniMember member = new UniMember();
                member.setUsername("Member" + i);
                member.setTeam(team);
                em.persist(member);
            }

            em.flush();
            em.clear(); // DB 동기화 후 캐시 비우기

            // ==========================================
            // 🚨 [Case A: 지옥문 개방] 일반 JPQL 조회 (N+1 발생)
            // ==========================================
            System.out.println("\n============== 🚨 [Case A] N+1 발생 ==============");
            // "난 분명 회원만 조회했는데..."
            List<UniMember> nPlusOneMembers = em.createQuery("select m from UniMember m", UniMember.class)
                    .getResultList();

            // 콘솔 확인: Member 조회 쿼리 1방 + Team 조회 쿼리 3방 = 총 4방의 쿼리가 연달아 찍힙니다!

            em.clear(); // 다시 캐시 비우기

            // ==========================================
            // 💊 [Case B: 해결책] Fetch Join 사용
            // ==========================================
            System.out.println("\n============== ✅ [Case B] Fetch Join으로 해결 ==============");
            // "회원 가져올 때 팀도 묶어서(join fetch) 한 번에 가져와!"
            List<UniMember> fetchJoinMembers = em.createQuery(
                            "select m from UniMember m join fetch m.team", UniMember.class)
                    .getResultList();

            // 콘솔 확인: INNER JOIN이 걸린 쿼리 딱 1방만 찍히며 모든 데이터를 완벽하게 가져옵니다!
            for (UniMember m : fetchJoinMembers) {
                System.out.println("회원: " + m.getUsername() + ", 팀: " + m.getTeam().getName());
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}