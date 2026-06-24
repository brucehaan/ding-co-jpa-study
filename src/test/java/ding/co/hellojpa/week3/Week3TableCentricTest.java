package ding.co.hellojpa.week3;

import ding.co.hellojpa.TableMember;
import ding.co.hellojpa.TableTeam;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
    public class Week3TableCentricTest {

        @Autowired
        EntityManagerFactory emf;

        @Test
        void 테이블_중심_설계의_한계_체험() {
            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();
            tx.begin();

            try {
                // ==========================================
                // 1. [저장] 데이터 셋업
                // ==========================================
                TableTeam team = new TableTeam();
                team.setName("개발팀");
                em.persist(team);
                // 영속 상태가 되어야 DB에 들어갈 ID 값이 생김

                TableMember member = new TableMember();
                member.setName("김철수");
                // 🤦‍♂️ 1차 불편함: 객체(team)를 넣지 못하고, 굳이 ID를 꺼내서 넣어야 함
                member.setTeamId(team.getId());
                em.persist(member);

                em.flush();
                em.clear(); // 영속성 컨텍스트 비우기

                // ==========================================
                // 2. [조회] 비극의 시작 (팀 이름 출력하기)
                // ==========================================
                System.out.println("=== 조회 시작 ===");

                // 1) 멤버를 조회함
                TableMember findMember = em.find(TableMember.class, member.getId());

                // 2) 팀 이름을 알고 싶네? 근데 findMember 안에는 teamId(숫자) 밖에 없음.
                // String teamName = findMember.getTeam().getName(); <-- (불가능! 객체 참조가 끊김)
                Long teamId = findMember.getTeamId();

                // 3) 🤦‍♂️ 2차 불편함: 팀을 다시 조회해야 함 (DB에 다시 물어봐야 함)
                TableTeam findTeam = em.find(TableTeam.class, teamId);

                // 4) 드디어 출력... 너무 힘들다.
                System.out.println("회원 이름 = " + findMember.getName());
                System.out.println("소속 팀 이름 = " + findTeam.getName());

                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                e.printStackTrace();
            } finally {
                em.close();
            }
        }
    }