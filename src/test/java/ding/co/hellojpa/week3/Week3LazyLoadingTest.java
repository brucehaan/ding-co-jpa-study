package ding.co.hellojpa.week3;

import ding.co.hellojpa.UniMember;
import ding.co.hellojpa.UniTeam;
import jakarta.persistence.*;
import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Week3LazyLoadingTest {

    @Autowired
    EntityManagerFactory emf;

    // ==========================================
    // Test 1. [실패] 트랜잭션 밖에서 프록시 초기화 시도 -> 예외 발생
    // ==========================================
    @Test
    void 레이지_로딩_예외_발생_테스트() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        Long memberId;
        try {
            // [데이터 준비]
            UniTeam team = new UniTeam();
            em.persist(team);
            UniMember member = new UniMember();
            member.setTeam(team);
            em.persist(member);

            memberId = member.getId();

            tx.commit(); // 트랜잭션 커밋
            em.close();  // ⭐️ 비서 퇴근 (영속성 컨텍스트 종료)

        } catch (Exception e) {
            tx.rollback();
            throw e;
        }

        // -------------------------------------------------------
        // [상황] 컨트롤러(View) 영역이라고 가정.
        // member는 이제 '준영속(Detached)' 상태입니다.
        // -------------------------------------------------------

        System.out.println("=== 1. 새로운 영속성 컨텍스트 시작 (조회용) ===");
        EntityManager viewEm = emf.createEntityManager();
        UniMember detachedMember = viewEm.find(UniMember.class, memberId);
        viewEm.close(); // ⭐️ 조회만 하고 바로 닫음 (준영속 확정)

        System.out.println("=== 2. 프록시 객체 확인 ===");
        // member.getTeam()은 진짜 UniTeam이 아니라 '프록시'입니다.
        System.out.println("team class = " + detachedMember.getTeam().getClass());
        // 출력 예: class ding.co.hellojpa.UniTeam$HibernateProxy$...

        System.out.println("=== 3. 팀 이름 조회 시도 (에러 발생 구간) ===");
        try {
            // 💥 여기서 에러 터짐!
            // "야, DB 가서 팀 이름 좀 가져와!" -> "저 연결 끊겼는데요? (no Session)"
            String teamName = detachedMember.getTeam().getName();
            System.out.println("팀 이름: " + teamName);

        } catch (LazyInitializationException e) {
            System.out.println(">>> ☠️ 예외 발생 성공! UniInitializationException");
            System.out.println(">>> 원인: 준영속 상태의 프록시를 초기화할 수 없음");
        }
    }

    // ==========================================
    // Test 2. [해결 1] 강제 초기화 (Hibernate.initialize)
    // ==========================================
    @Test
    void 강제_초기화로_해결_테스트() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // [데이터 준비]
            UniTeam team = new UniTeam();
            em.persist(team);
            UniMember member = new UniMember();
            member.setTeam(team);
            em.persist(member);

            em.flush();
            em.clear();

            System.out.println("=== 1. 조회 및 강제 초기화 ===");
            UniMember findMember = em.find(UniMember.class, member.getId());

            // [해결책] 트랜잭션 안에서 미리 찔러보기 (초기화)
            System.out.println("-> Hibernate.initialize() 호출");
            Hibernate.initialize(findMember.getTeam());

            // 이때 SELECT 쿼리가 나갑니다. (프록시 -> 진짜 데이터 채움)

            tx.commit();
            em.close(); // 트랜잭션 종료

            System.out.println("=== 2. 트랜잭션 밖에서 조회 ===");
            // 이미 안에서 다 가져왔으므로, 밖에서도 에러 없이 조회 가능!
            System.out.println("팀 이름: " + findMember.getTeam().getName());
            System.out.println(">>> 🎉 성공! 미리 초기화해둬서 에러 안 남");

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
    }

    // ==========================================
    // Test 3. [해결 2] Fetch Join (가장 권장됨 ⭐)
    // ==========================================
    @Test
    void 페치조인으로_해결_테스트() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // [데이터 준비]
            UniTeam team = new UniTeam();
            em.persist(team);
            UniMember member = new UniMember();
            member.setTeam(team);
            em.persist(member);

            em.flush();
            em.clear();

            System.out.println("=== 1. Fetch Join 조회 ===");

            // [해결책] JPQL로 "처음부터 멤버랑 팀을 같이(Join Fetch) 가져와!" 라고 함
            // -> SQL 한 방에 해결됨
            UniMember findMember = em.createQuery(
                            "select m from UniMember m join fetch m.team where m.id = :id",
                            UniMember.class)
                    .setParameter("id", member.getId())
                    .getSingleResult();

            System.out.println("team class = " + findMember.getTeam().getClass());
            // 출력: class ding.co.hellojpa.UniTeam (프록시 아님! 진짜 객체!)

            tx.commit();
            em.close(); // 트랜잭션 종료

            System.out.println("=== 2. 트랜잭션 밖에서 조회 ===");
            System.out.println("팀 이름: " + findMember.getTeam().getName());
            System.out.println(">>> 🎉 성공! Fetch Join 덕분에 진짜 객체가 들어있음");

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
    }
}