package ding.co.hellojpa.week3;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class Week3LazyLoadingTest {
    @Autowired
    EntityManagerFactory emf;

    // 1. 실패 - 트랜잭션 밖에서 프록시 초기화 시도 -> 예외 발생
    @Test
    void 레이지로딩_예외발생_테스트() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        Long memberId = 1L;
        try {
            // 데이터 준비
            UniTeam team = new UniTeam();
            em.persist(team);
            UniMember member = new UniMember();
            member.setTeam(team);
            em.persist(member);

            memberId = member.getId();
            tx.commit(); // 트랜잭션 커밋
            em.close(); // 비서 퇴근 (영속성 컨텍스트 종료)
        }  catch (Exception e) {
            tx.rollback();
        }

        // 상황 : 컨트롤러(View)영역이라고 가정.
        // member는 이제 '준영속(Detached)'상태
        log.info("1. 새로운 영속성 컨텍스트 시작(조회용)");
        EntityManager viewEm = emf.createEntityManager();
        UniMember detachedMember = viewEm.find(UniMember.class, memberId);
        viewEm.close(); // 조회만 하고 바로 닫음(준영속 확정)

        log.info("2. 프록시 객체 확인");
        // member.getTeam()은 진짜 UniTeam이 아니라 '프록시'다.
        log.info("team class = {}", detachedMember.getTeam().getClass());
        // 출력 예: class ding.co.hellojpa.UniTeam$HibernateProxy$...

        log.info("3. 팀 이름 조회 시도 (에러 발생 구간)");
        try {
            // 여기서 에러 터짐
            // DB가서 팀 이름 좀 가져와 -> 저 연결 끊겼는데요? (no session)
            String teamName = detachedMember.getTeam().getName();
            log.info("팀 이름 : {}", teamName);
        } catch (LazyInitializationException e) {
            log.info("예외 발생 성공 : UnInitializationException");
            log.info("원인 : 준영속 상태의 프록시를 초기화할 수 없음");
        }
    }

    // 2. 해결 1. 강제 초기화 (Hibernate.initialize)
    @Test
    void 강제초기화로해결_테스트() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 데이터 준비
            UniTeam team = new UniTeam();
            em.persist(team);
            UniMember member = new UniMember();
            member.setTeam(team);
            em.persist(member);

            em.flush();
            em.clear();

            log.info("1. 조회 및 강제 초기화");
            UniMember findMember = em.find(UniMember.class, member.getId());

            // 해결책 : 트랜잭션 안에서 미리 찔러보기(초기화)
            log.info("Hibernate.initialize 호출()");
            Hibernate.initialize(findMember.getTeam());

            // 이때 select 쿼리가 나감 (프록시 -> 진짜 데이터 채움)

            tx.commit();
            em.close(); // 트랜잭션 종료

            log.info("2. 트랜잭션 밖에서 조회");
            // 이미 안에서 다 가져왔으므로, 밖에서도 에러 없이 조회 가능
            log.info("팀 이름 : {}", findMember.getTeam().getName());
            log.info("성공 : 미리 초기화해둬서 에러 안 남");
        } catch (Exception e) {
            tx.rollback();
        }
    }

    // Test3. 해결2. Fetch Join (가장 권장됨)
    @Test
    void 페치조인으로해결_테스트() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 데이터 준비
            UniTeam team = new UniTeam();
            em.persist(team);
            UniMember member = new UniMember();
            member.setTeam(team);
            em.persist(member);

            em.flush();
            em.clear();

            log.info("1. fetch join 조회");

            // ㅎ해결책 : JPQL로 처음부터 멤버랑 팀을 같이 (JOin fetch) -> SQL 한 방에 해결됨
            UniMember findMember = em.createQuery(
                    "select m from UniMember m join fetch m.team where m.id = :id",
                    UniMember.class)
                    .setParameter("id", member.getId())
                    .getSingleResult();

            log.info("team class {}", findMember.getTeam().getClass());
            // 출력 : class ding.co.hellojpa.UniTeam(프록시 아님. 진짜 객체임)

            tx.commit();
            em.close();

            log.info("2. 트랜잭션 밖에서 조회");
            log.info("팀 이름 : {}", findMember.getTeam().getName());
            log.info("성공 : fetch join 덕분에 진짜 객체가 들어있음");
        } catch (Exception e) {
            tx.rollback();
        }
    }
}
