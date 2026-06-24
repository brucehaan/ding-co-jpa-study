package ding.co.hellojpa.week3;

import jakarta.persistence.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Week3OneToOneTest {

    @Autowired
    EntityManagerFactory emf;

    @Test
    void 일대일_주테이블_FK_지연로딩_완벽동작_테스트() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            System.out.println("=== 1. 데이터 세팅 및 저장 ===");
            // 1. 대상 테이블(Locker) 저장
            Locker locker = new Locker();
            locker.setName("강남역 1번 사물함");
            em.persist(locker);

            // 2. 주 테이블(Member) 저장
            OneToOneMember member = new OneToOneMember();
            member.setName("김회원");

            // ✅ [정답] 연관관계의 주인(FK 보유자)에게 값을 설정!
            member.setLocker(locker);
            em.persist(member);

            // DB에 쿼리를 날리고 영속성 컨텍스트 비우기 (조회 테스트를 위해)
            em.flush();
            em.clear();

            System.out.println("\n=== 2. 회원 단건 조회 시작 (LAZY 동작 확인) ===");
            // 주 테이블(Member)에 FK가 있기 때문에, JPA는 DB에서 Member만 딱 가져오고
            // Locker 자리는 프록시(가짜 객체)로 채워둘 수 있습니다!
            OneToOneMember findMember = em.find(OneToOneMember.class, member.getId());

            System.out.println("회원 조회 완료. 이름: " + findMember.getName());

            System.out.println("\n=== 3. 프록시 객체 확인 ===");
            // 아직 사물함 테이블은 조회하지 않았습니다. 가짜 객체인지 확인해볼까요?
            System.out.println("Locker 객체 타입: " + findMember.getLocker().getClass().getName());

            System.out.println("\n=== 4. 실제 사물함 데이터 접근 (초기화 발생) ===");
            // getName()을 호출하는 순간! 진짜 데이터가 필요해져서 DB에 SELECT 쿼리가 나갑니다.
            System.out.println("배정된 사물함 이름: " + findMember.getLocker().getName());

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}