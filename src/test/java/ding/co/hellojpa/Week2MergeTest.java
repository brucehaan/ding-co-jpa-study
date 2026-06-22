package ding.co.hellojpa;

import jakarta.persistence.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Week2MergeTest {

    @Autowired
    EntityManagerFactory emf;

    @Test
    void 병합_merge_동작원리_검증() {
        // -------------------------------------------------------
        // 1. [준비] DB에 데이터 저장 후 비서 퇴근 (준영속 만들기)
        // -------------------------------------------------------
        EntityManager em1 = emf.createEntityManager();
        EntityTransaction tx1 = em1.getTransaction();
        tx1.begin();

        Member member = new Member("OriginalName", Grade.BASIC);
        em1.persist(member);
        Long memberId = member.getId();

        tx1.commit();
        em1.close(); // ⭐️ 영속성 컨텍스트 종료! (member는 이제 준영속 상태)

        System.out.println("=== 1. 준영속 상태 진입 ===");
        // member: ID는 있지만, JPA가 관리는 안 하는 종이 쪼가리

        // -------------------------------------------------------
        // 2. [수정 시도] 준영속 객체의 값을 변경
        // -------------------------------------------------------
        System.out.println("-> 이름을 'UpdatedName'으로 변경 시도");
        member.setName("UpdatedName");
        // (이 상태로는 DB에 절대 반영 안 됨)

        // -------------------------------------------------------
        // 3. [Merge] 비서에게 제출 (새 트랜잭션)
        // -------------------------------------------------------
        EntityManager em2 = emf.createEntityManager();
        EntityTransaction tx2 = em2.getTransaction();
        tx2.begin();

        System.out.println("=== 2. merge() 호출 ===");

        // [핵심] merge 호출
        // member: 내가 가져온 종이 (준영속, 그대로임)
        // mergedMember: 비서가 DB에서 찾아내서 내용을 고친 '진짜 원본' (영속)
        Member mergedMember = em2.merge(member);

        // [검증 1] 두 객체는 같은 놈인가? (다름!)
        System.out.println("member(내 종이) 관리 여부: " + em2.contains(member)); // false
        System.out.println("mergedMember(받은 원본) 관리 여부: " + em2.contains(mergedMember)); // true

        if (member != mergedMember) {
            System.out.println(">>> 💡 확인: merge()는 내 객체를 영속화하는 게 아니라, '새로운 복사본'을 반환합니다!");
        }

        tx2.commit(); // 커밋 시점에 Update 쿼리 발생
        System.out.println("=== 3. 커밋 완료 (DB 반영됨) ===");
        em2.close();
    }

    @Test
    void 병합의_치명적_함정_null_덮어쓰기() {
        // -------------------------------------------------------
        // 1. [준비] 완벽한 정보를 가진 회원 저장 (이름 + 등급)
        // -------------------------------------------------------
        EntityManager em1 = emf.createEntityManager();
        EntityTransaction tx1 = em1.getTransaction();
        tx1.begin();

        Member original = new Member("UserA", Grade.VIP); // 이름: UserA, 등급: VIP
        em1.persist(original);
        Long id = original.getId();

        tx1.commit();
        em1.close();

        // -------------------------------------------------------
        // 2. [함정] 수정하고 싶은 데이터만 가진 객체 생성
        // -------------------------------------------------------
        System.out.println("=== 💀 merge의 함정 테스트 시작 ===");

        // 상황: 화면에서 이름만 "UserB"로 바꾸고 싶어서 객체를 새로 만들었음.
        // 근데 실수로 Grade(등급)는 세팅을 안 함 (null 상태)
        Member detachedMember = new Member();
        detachedMember.setId(id); // ID는 필수
        detachedMember.setName("UserB");
        // detachedMember.setGrade(null); // ⚠️ 등급은 null인 상태!

        // -------------------------------------------------------
        // 3. [Merge] 수행
        // -------------------------------------------------------
        EntityManager em2 = emf.createEntityManager();
        EntityTransaction tx2 = em2.getTransaction();
        tx2.begin();

        System.out.println("-> merge() 수행 (이름만 바꾸려고 했으나...)");

        // JPA는 detachedMember의 모든 필드(null 포함)를 원본에 덮어씁니다.
        em2.merge(detachedMember);

        tx2.commit();
        em2.close();

        // -------------------------------------------------------
        // 4. [결과 확인] 대참사 확인
        // -------------------------------------------------------
        EntityManager em3 = emf.createEntityManager();
        Member findMember = em3.find(Member.class, id);

        System.out.println("DB 저장된 이름: " + findMember.getName()); // UserB (변경됨)
        System.out.println("DB 저장된 등급: " + findMember.getGrade()); // 😱 null (사라짐!)

        if (findMember.getGrade() == null) {
            System.out.println(">>> ☠️ 대참사 발생! VIP 등급이 null로 증발했습니다.");
            System.out.println(">>> (교훈: 수정할 때는 merge 쓰지 마세요. Dirty Checking 쓰세요.)");
        }

        em3.close();
    }
}