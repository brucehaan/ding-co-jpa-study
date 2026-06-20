package ding.co.hellojpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest // 스프링 부트의 모든 설정을 가져와서 테스트
@Slf4j
public class PersistenceContextTest {
    @Autowired
    EntityManagerFactory emf; // 공장을 주입받는다.

    @Test
    void 영속성_컨텍스트_생존확인() {
        // 1. 비서(EntityManager) 생성
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        // 2. 업무 시작
        tx.begin();

        try {
            // [준비] DB에 데이터가 없으니 1번 유저를 미리 저장해둡니다.
            User initialUser = new User();
            // initialUser.setId(1L); // IDENTITY 전략이면 생략 가능
            initialUser.setName("UserA");
            em.persist(initialUser);

            // 영속성 컨텍스트를 비우고(DB에 반영하고) 초기화 (실험을 위해 깨끗하게!)
            em.flush();
            em.clear();

            log.info("=== 🚀 실험 시작 ===");

            // 3. 비서에게 1번 회원 조회를 시킴
            // (DB에 갔다가 캐시에 저장함)
            User user = em.find(User.class, 1L);
            log.info("-> 1번 조회 완료 (DB 쿼리 나감)");

            // 4. 이름 변경 (비서에게 "이름 바꿔"라고 말만 함)
            user.setName("JPA Master");
            log.info("-> setName 호출 완료 (쿼리 안 나감!)");
            // -> 중요! 여기서 UPDATE 쿼리가 나가지 않습니다!

            log.info("=== 🛑 커밋 직전 ===");

            // 5. 업무 종료 (커밋)
            tx.commit();
            log.info("-> 커밋 완료 (이때 UPDATE 쿼리 실행)");

        } catch (Exception e) {
            tx.rollback(); // 에러 나면 취소
            e.printStackTrace();
        } finally {
            em.close(); // 비서 퇴근 (중요!)
        }
    }

    @Test
    void 일차캐시_동작_검증() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        // 1. 업무 시작 (트랜잭션)
        tx.begin();

        try {
            // [준비] 1차 캐시 실험을 위해 데이터 1개를 미리 저장해둡니다.
            User user = new User("CacheUser");
            em.persist(user); // em.persist 를 한다고 해서 db에 바로 저장하는 것은 아니다.

            // db에서 값을 뽑아줘야지만 id 필드를 넣어줄 수 있는 상태이기 때문에, 이 AUTO_INCREMENT는 예외적으로 바로 db Insert가 나간다.

            // 쿼리를 모아놨다가 한 번에 호출을 해야 db connection을 최대한 아낄 수 있음. 그래서 insert 쿼리가 바로 수행이 안 된다.
            Long userId = user.getId(); // 저장되면서 생성된 ID(1L)를 확보

            // 영속성 컨텍스트를 싹 비웁니다. (초기화)
            // private HashMap<EntityKey, EntityHolderImpl> entitiesByKey;
            // HashMap key = User::1 -> value = user(id=1, name="CacheUser")
            // persist 메서드하면서 저장했었던 객체가 있기 때문에
            // em.find(User::Class, 1) -> entitiesByKey -> User::1에 대한 Value값 존재하니?
            // 그 값을 바로 반환하려 들 거다.

            // 비워야 DB에서 처음 조회하는 상황을 재현할 수 있으니까요!
            em.flush(); // 일단 머릿속에 저장해놓은 (map에 들어가있는 데이터를) db에다가 확실하게 넣어둔다. 반영해버린다.
            em.clear(); // 머리를 텅 비우는 초기화 작업

            log.info("=== 🚀 1차 캐시 실험 시작 ===");

            // 1. 첫 번째 조회: 캐시에 없음 -> DB 쿼리 전송 (SELECT 1회 발생)
            log.info("1. 첫 번째 조회 요청");
            User findUser1 = em.find(User.class, userId);
            log.info("-> 조회 완료 (영속성 컨텍스트에 저장됨)");

            // 2. 두 번째 조회: 캐시에 있음 -> 메모리에서 바로 반환 (SELECT 0회)
            // 비서(EntityManager)가 "아까 찾은 거 여기 있어요" 하고 바로 줍니다.
            log.info("2. 두 번째 조회 요청");
            User findUser2 = em.find(User.class, userId);
            log.info("-> 조회 완료 (캐시에서 가져옴)");

            // 3. 세 번째 조회: 역시 캐시에 있음 -> (SELECT 0회)
            log.info("3. 세 번째 조회 요청");
            User findUser3 = em.find(User.class, userId);
            log.info("-> 조회 완료 (캐시에서 가져옴)");

            // 4. 동일성 비교 (중요!)
            // 자바 컬렉션에서 꺼낸 것처럼 주소값까지 똑같습니다.
            log.info("=== 🧪 검증 결과 ===");
            log.info("findUser1 == findUser2 : " + (findUser1 == findUser2)); // true

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Test
    void 비즈니스_로직_공유_검증() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // [준비] 데이터 저장
            User user = new User("BusinessUser");
            em.persist(user);
            Long userId = user.getId();

            // 영속성 컨텍스트 초기화 (DB 조회 상황 연출)
            em.flush();
            em.clear();

            log.info("=== 🚀 비즈니스 로직 시작 ===");

            // 1. 메인 로직에서 조회 (SQL 발생 O)
            log.info("1. mainLogic()에서 조회");
            User m1 = em.find(User.class, userId);

            log.info("... (수백 줄의 복잡한 코드 실행 중) ...");

            // 2. 다른 메서드를 호출함 (ID만 넘김)
            // 개발자 고민: "m1 객체를 넘겨야 하나? 아니면 ID만 넘겨서 다시 조회해도 되나?"
            // 정답: "ID만 넘겨도 된다! 어차피 캐시에서 가져오니까!"
            subLogic(em, userId);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // 다른 비즈니스 로직을 담당하는 메서드 (가정)
    private void subLogic(EntityManager em, Long userId) {
        log.info("2. subLogic() 진입 - 동일한 유저 필요");

        // 여기서 또 find를 호출해도 성능 부담이 '전혀' 없음
        User m2 = em.find(User.class, userId);

        log.info("-> subLogic 조회 완료 (쿼리 안 나감!)");
        log.info("-> m2 이름 확인: " + m2.getName());
    }

    @Test
    void 엔티티_동일성_보장_검증() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // [준비] 데이터 1개 저장
            User user = new User("IdentityUser");
            em.persist(user);
            Long userId = user.getId();

            // 영속성 컨텍스트를 비웁니다. (DB에 반영 후 1차 캐시 삭제)
            // -> 이렇게 해야 find를 호출할 때 DB에서 새로 가져오는 상황이 됩니다.
            em.flush();
            em.clear();

            log.info("=== 🚀 동일성 비교 실험 시작 ===");

            // 1. 첫 번째 조회 (DB에서 가져와서 1차 캐시에 넣음)
            User findUser1 = em.find(User.class, userId);
            log.info("findUser1 참조값: " + System.identityHashCode(findUser1));

            // 2. 두 번째 조회 (1차 캐시에 있는 걸 그대로 줌)
            User findUser2 = em.find(User.class, userId);
            log.info("findUser2 참조값: " + System.identityHashCode(findUser2));

            // 3. == 비교 (주소값 비교)
            log.info("=== 🧪 결과 확인 ===");
            boolean isSame = (findUser1 == findUser2);

            log.info("findUser1 == findUser2 : " + isSame);

            // JUnit 단언문으로도 검증 (실패하면 테스트 빨간불 뜸)
            if (!isSame) {
                throw new RuntimeException("테스트 실패! 두 객체가 다릅니다.");
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Test
    void 자바_컬렉션처럼_다루기_List편() {
        System.out.println("=== ☕ 자바 List 실험 시작 ===");

        // 1. 리스트 생성 (DB라고 상상하세요)
        List<Member> list = new ArrayList<>();

        // 2. 데이터 저장
        Member member = new Member("OldName", Grade.BASIC);
        list.add(member); // INSERT

        // 3. 데이터 꺼내기 (SELECT)
        // 리스트 안에 있는 객체의 참조값(주소)을 가져옵니다.
        Member findMember = list.get(0);

        // 4. 데이터 수정
        System.out.println("-> 이름을 'NewName'으로 변경");
        findMember.setName("NewName");

        // 5. 다시 리스트에 넣나요? (중요!)
        // list.add(findMember);  <-- 이런 짓 안 하죠?
        // list.update(0, findMember); <-- 이런 메서드도 없습니다!

        // 6. 검증
        // 꺼낸 놈을 바꿨는데, 리스트 안에 있는 놈도 바뀌었을까?
        Member storedMember = list.get(0);
        System.out.println("리스트에 저장된 이름: " + storedMember.getName());

        if ("NewName".equals(storedMember.getName())) {
            System.out.println(">>> ✅ 당연히 바뀌어 있음! (참조값 공유)");
        }
    }

    @Test
    void 자바_컬렉션처럼_다루기_JPA편() {
        System.out.println("=== 🚀 JPA 실험 시작 ===");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 1. 데이터 저장 (List.add와 비슷)
            Member member = new Member("OldName", Grade.BASIC);
            em.persist(member);
            Long id = member.getId();

            em.flush();
            em.clear(); // (실험을 위해 DB에 반영하고 메모리 비움)

            // 2. 데이터 꺼내기 (List.get과 비슷)
            Member findMember = em.find(Member.class, id);

            // 3. 데이터 수정
            System.out.println("-> 이름을 'NewName'으로 변경");
            findMember.setName("NewName");

            // 4. 다시 저장하나요? (핵심!)
            // em.persist(findMember); <-- 필요 없음! (List에도 다시 안 넣었잖아?)
            // em.update(findMember);  <-- 이런 메서드 자체가 없음!

            System.out.println("=== 🛑 커밋 직전 (자동 감지) ===");
            tx.commit(); // 5. 커밋 (이때 JPA가 List처럼 동작해서 DB에 반영함)

            System.out.println(">>> ✅ UPDATE 쿼리 자동 발생함!");

        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
    }
}
