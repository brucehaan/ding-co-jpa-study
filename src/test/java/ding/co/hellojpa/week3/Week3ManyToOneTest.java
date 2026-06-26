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
public class Week3ManyToOneTest {
    @Autowired
    EntityManagerFactory emf;

    @Test
    void 단방향_연관관계_저장_조회_수정_테스트() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            log.info("1. 저장 (Save) ");
            // 1. 팀 저장
            UniTeam team = new UniTeam();
            team.setName("TeamA");
            em.persist(team); // 영속 상태가 되어야 ID가 생김

            // 2. 회원 저장
            UniMember member = new UniMember();
            member.setUsername("member1");

            /*
            핵심 : 참조를 그냥 넣어버린다
            JPA가 알아서 team에서 ID(PK)를 꺼내 UNI_MEMBER테이블의 TEAM_ID(FK)에 넣음
             */
            member.setTeam(team);
            em.persist(member);

            // 조회를 위해 영속성 컨텍스트 비우기 (DB에 쿼리 전송)
            em.flush();
            em.clear();

            log.info("2. 조회(read) - 객체 그래프 탐색 ");
            // 1. 회원 조회
            UniMember findMember = em.find(UniMember.class, member.getId());

            // 2. 참조를 사용해서 팀 조회 (객체 그래프 탐색)
            // SQL 조인 쿼리 없이, 마치 메모리에 있는 객체 꺼내듯이 가져옴 (지연 로딩 발동)
            UniTeam findTeam = findMember.getTeam();
            log.info("findTeam = {}", findTeam.getName());

            log.info("3. 수정(update) - 팀 옮기기");
            // 회원이 팀을 옮기고 싶으면 어떻게 할까요? db 쿼리를 날릴 필요가 없습니다.

            // 새로운 팀 생성
            UniTeam newTeam = new UniTeam();
            newTeam.setName("TeamB");
            em.persist(newTeam);

            // 더티 체킹 (dirty checking)
            // 그냥 set 메서드로 객체만 바꿔치기하면 끝남
            findMember.setTeam(newTeam);
            log.info("변경된 팀 이름 = {}", findMember.getTeam().getName());

            // 트랜잭션 커밋 시점에 UPDATE UNI_MEMBER SET TEAM_ID = ? 쿼리가 나감
            tx.commit();
            log.info("트랜잭션 커밋 완료 (update 쿼리 확인)");
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
