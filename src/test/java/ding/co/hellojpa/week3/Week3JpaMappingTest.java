package ding.co.hellojpa.week3;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class Week3JpaMappingTest {
    @Autowired
    EntityManagerFactory emf;

    @Test
    void 객체지향적인_JPA_매핑_체험() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            /**
             * 1. 저장 - 객체를 그냥 넣어버림
             */
            JpaTeam team = new JpaTeam();
            team.setName("JPA개발팀");
            em.persist(team); // DB에 저장 (ID 생성)

            JpaMember member = new JpaMember();
            member.setName("김JPA");

            // 감동 포인트 1 : ID를 꺼내지 않고 객체를 통째로 넘김
            // JPA가 알아서 tema의 PK(id)를 꺼내서 MEMBER 테이블의 TEAM_ID(FK)로 저장한다.
            member.setTeam(team);

            em.persist(member);

            em.flush();
            em.clear(); // 영속성 컨텍스트 비우기

            log.info("2. 조회 시작 - 객체 그래프 탐색");
            /**
             * 2. 조회 - 참조로 바로 꺼냄
             */
            JpaMember findMember = em.find(JpaMember.class, member.getId());

            // 감동 포인트 2 : 다시 em.find()를 할 필요가 없음
            // member.getTeam()을 호출하는 순간, 참조를 타고 넘어간다. (FK 조인 자동 처리)
            JpaTeam findTeam = findMember.getTeam();

            log.info("회원 이름 : {} ", findMember.getName());
            log.info("소속 팀 이름 : {} ", findTeam.getName());

            // 검증 로직
            if ("JPA개발팀".equals(findTeam.getName())) {
                log.info("성공 : 참조만으로 연관된 데이터를 가져옴");
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
