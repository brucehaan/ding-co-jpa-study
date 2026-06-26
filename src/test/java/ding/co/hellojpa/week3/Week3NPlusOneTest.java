package ding.co.hellojpa.week3;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest
public class Week3NPlusOneTest {
    @Autowired
    EntityManagerFactory emf;

    @Test
    void N플러스1_문제발생과_해결테스트() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 데이터 준비. 팀 3개, 각 팀에 회원 1명씩 저장
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

            // case a : 지옥문 개방 - 일반 JPQL 조회 (N+1 발생)
            log.info("case a : N + 1 발생");
            List<UniMember> nPlusOneMembers = em.createQuery(
                    "select m from UniMember m", UniMember.class)
                    .getResultList();

            // 콘솔 확인 : Member 조회 쿼리 1방 + Team 조회 쿼리 3방 = 총 4방의 쿼리가 연달아 찍힌다
            em.clear();

            // case b 해결책 : fetch join 사용
            log.info("case b : Fetch Join 으로 해결");
            List<UniMember> fetchJoinMembers = em.createQuery(
                    "select m from UniMember m join fetch m.team", UniMember.class)
                    .getResultList();

            // 콘솔 확인 : INNER JOIN이 걸린 쿼리 딱 1방만 찍히며 모든 데이터를 완벽하게 가져옴
            for (UniMember m : fetchJoinMembers) {
                log.info("회원 : {}, 팀 : {}", m.getUsername(), m.getTeam().getName());
            }
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
    }
}
