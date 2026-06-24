package ding.co.hellojpa.week3;

import ding.co.hellojpa.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class Week3OneToManyTest {

    @Autowired
    EntityManagerFactory emf;

    @Test
    void 일대다_단방향_치명적단점_테스트() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 1. 회원 생성
            OneToManyMember member = new OneToManyMember();
            member.setName("회원1");
            em.persist(member); // INSERT 쿼리 (TEAM_ID는 null로 들어감)

            // 2. 팀 생성 및 연관관계 설정
            OneToManyTeam team = new OneToManyTeam();
            team.setName("TeamA");
            team.getMembers().add(member); // 리스트에 넣음

            em.persist(team); // INSERT 쿼리

            System.out.println("=== 트랜잭션 커밋 시점 (문제 발생) ===");
            tx.commit();
            // JPA: "어? Team이 member를 데리고 있네? 근데 FK는 저쪽 MEMBER 테이블에 있잖아? 업데이트해!"
            // 여기서 불필요한 UPDATE 쿼리가 추가로 발생함!

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
