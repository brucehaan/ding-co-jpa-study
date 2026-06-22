package ding.co.hellojpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.FlushModeType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class Week2PersistenceContextTest {

    @Autowired
    EntityManagerFactory emf;

    @Test
    void JPQL_자동_플러시_동작_검증() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            System.out.println("=== 1. 영속성 컨텍스트에 저장 (쿼리 안 나감) ===");
            SequenceMember memberA = new SequenceMember("SequenceMemberA");
            SequenceMember memberB = new SequenceMember("SequenceMemberB");
            SequenceMember memberC = new SequenceMember("SequenceMemberC");

            // 1차 캐시에만 저장된 상태 (쓰기 지연 SQL 저장소에 INSERT 쿼리 대기 중)
            em.persist(memberA);
            em.persist(memberB);
            em.persist(memberC);

            // (참고: 아직 Insert 쿼리는 DB로 안 넘어갔음!)

            System.out.println("=== 2. JPQL 쿼리 실행 직전 ===");

            // [핵심] JPQL 실행
            // JPA의 판단: "어? 쿼리를 날려야 하네? 근데 아까 persist 한 거 아직 안 보냈잖아?"
            //             "이거 먼저 보내야(Flush) DB에서 조회가 되겠구나!" -> 강제 Flush 발동 🚀
            // (JPQL은 항상 DB에 직접 SQL을 날리기 때문에, 영속성 컨텍스트에 있는 걸 먼저 밀어 넣습니다.)
            List<SequenceMember> result = em.createQuery("select m from SequenceMember m", SequenceMember.class)
                    .getResultList();

            System.out.println("=== 3. JPQL 쿼리 실행 완료 ===");

            System.out.println("조회된 회원 수: " + result.size() + "명");

            // 검증: 방금 넣은 3명이 조회되어야 정상!
            boolean foundA = false;
            for (SequenceMember m : result) {
                if ("SequenceMemberA".equals(m.getName())) {
                    foundA = true;
                    break;
                }
            }

            if (foundA) {
                System.out.println(">>> ✅ 성공! 방금 저장한 데이터가 조회되었습니다.");
            } else {
                System.out.println(">>> ❌ 실패! 데이터가 조회되지 않았습니다. (Phantom Read)");
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // ==========================================
    // 2. 수동 플러시: "내가 원할 때 쏜다"
    // ==========================================
    @Test
    void 수동_플러시_테스트() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            System.out.println("=== [Test 2] 수동 플러시 시작 ===");

            SequenceMember member = new SequenceMember("ManualFlush");
            em.persist(member);

            // 원래라면 여기서 쿼리가 안 나갑니다. (쓰기 지연)
            System.out.println("============== (1) 플러시 전 (쿼리 없음) ==============");

            // ✨ 강제 호출! -> 쓰기 지연 저장소에 있는 쿼리를 즉시 DB로 보냄
            System.out.println("-> em.flush() 호출!");
            em.flush();

            System.out.println("============== (2) 플러시 후 (쿼리 나감) ==============");

            System.out.println("============== (3) 커밋 호출 ==============");
            tx.commit(); // 이미 보냈으므로 여기서는 쿼리가 안 나감 (확정만 함)

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // ==========================================
    // 3. 플러시와 1차 캐시: "비우는 게 아니라니까요"
    // ==========================================
    @Test
    void 플러시_후_1차캐시_생존_확인() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            System.out.println("=== [Test 3] 1차 캐시 생존 확인 시작 ===");

            SequenceMember member = new SequenceMember("CacheCheck");
            em.persist(member); // 1차 캐시 저장
            Long id = member.getId();

            System.out.println("============== 플러시 실행 (DB 동기화) ==============");
            em.flush(); // DB에 SQL 전송. 하지만 1차 캐시는 그대로 있음!

            System.out.println("============== 같은 ID로 조회 (Select 나갈까?) ==============");
            // 과연 DB에서 다시 가져올까요? (SELECT 쿼리가 나갈까?)
            // 정답: 안 나감. 1차 캐시가 살아있으니까.
            SequenceMember findSequenceMember = em.find(SequenceMember.class, id);

            System.out.println("조회된 이름: " + findSequenceMember.getName());

            // 검증: 쿼리가 안 나가고, 객체의 주소가 같아야 함
            if (member == findSequenceMember) {
                System.out.println(">>> ✅ 1차 캐시 생존 확인! (주소값이 같음)");
            } else {
                System.out.println(">>> ❌ 실패.. 객체가 다릅니다.");
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // ==========================================
    // 4. [심화] FlushModeType.COMMIT (문제 상황 재현)
    // ==========================================
    @Test
    void 플러시모드_COMMIT_변경시_문제점() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            System.out.println("=== [Test 4] 플러시 모드 COMMIT 변경 테스트 ===");

            // ⚠️ 설정을 COMMIT으로 변경 (커밋할 때만 플러시 하겠다!)
            // -> 이제 JPQL을 실행해도 자동으로 플러시를 안 합니다.
            em.setFlushMode(FlushModeType.COMMIT);

            SequenceMember member = new SequenceMember("NoFlushSequenceMember");
            em.persist(member);

            System.out.println("============== JPQL 실행 (플러시 안 함) ==============");

            // 모드가 COMMIT이라서 Insert를 안 날리고 그냥 Select만 날림
            // DB에는 "NoFlushSequenceMember"가 없으므로 조회 결과 0개
            List<SequenceMember> list = em.createQuery("select m from SequenceMember m where m.name = 'NoFlushSequenceMember'", SequenceMember.class)
                    .getResultList();

            System.out.println("조회된 크기: " + list.size()); // 0 출력됨

            if (list.size() == 0) {
                System.out.println(">>> 😱 예상대로 데이터가 조회되지 않았습니다! (데이터 불일치)");
                System.out.println(">>> (결론: 특별한 이유 없으면 AUTO(기본값)를 쓰세요)");
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // ==========================================
    // 5. 엔티티 생명주기: 비영속 -> 영속 -> 준영속 -> 삭제
    // ==========================================
    @Test
    void 엔티티_생명주기_검증() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 1. [비영속 상태] (Transient)
            // 객체를 생성만 한 상태. JPA와 전혀 관계 없음.
            System.out.println("=== 1. 비영속 상태 (New) ===");
            Member member = new Member("LifeCycleMember", Grade.BASIC);

            // 검증: 영속성 컨텍스트에 있니? -> 아니요(false)
            boolean isManaged1 = em.contains(member);
            System.out.println("관리 대상인가요? " + isManaged1); // false


            // 2. [영속 상태] (Managed)
            // 객체를 저장함. 이제부터 JPA가 관리함 (1차 캐시에 들어감)
            System.out.println("=== 2. 영속 상태 (Managed) ===");
            em.persist(member);

            // 검증: 영속성 컨텍스트에 있니? -> 예(true)
            boolean isManaged2 = em.contains(member);
            System.out.println("관리 대상인가요? " + isManaged2); // true


            // 3. [준영속 상태] (Detached)
            // 영속성 컨텍스트에서 강제로 쫓아냄.
            System.out.println("=== 3. 준영속 상태 (Detached) ===");
            em.detach(member);

            // 검증: 영속성 컨텍스트에 있니? -> 아니요(false)
            boolean isManaged3 = em.contains(member);
            System.out.println("관리 대상인가요? " + isManaged3); // false

            // [실험] 준영속 상태에서 값을 바꾸면?
            System.out.println("-> 이름 변경 시도 (DetachedMember)");
            member.setName("DetachedMember");
            // 결과: 비서(JPA)가 퇴근했으므로 변경 감지(Dirty Checking)가 동작하지 않음!
            // 즉, DB에 UPDATE 쿼리가 안 나감.


            // 4. [삭제 상태] (Removed)
            // 삭제를 하려면 다시 영속 상태에 있어야 함 (find or merge)
            System.out.println("=== 4. 삭제 상태 (Removed) ===");

            // 다시 조회 (DB -> 영속성 컨텍스트)
            Member findMember = em.find(Member.class, member.getId());

            em.remove(findMember); // 삭제 요청
            System.out.println("-> 삭제 요청됨 (커밋 시 Delete 쿼리 나감)");

            // 참고: remove를 호출해도 커밋 전까지는 1차 캐시에는 남아있을 수 있지만,
            // 실무적으로는 "사용 불가" 상태로 봅니다.

            tx.commit();
            System.out.println("=== 커밋 완료 ===");

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}