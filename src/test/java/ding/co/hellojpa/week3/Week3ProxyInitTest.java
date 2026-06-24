package ding.co.hellojpa.week3;

import ding.co.hellojpa.UniMember;
import jakarta.persistence.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Week3ProxyInitTest {

    @Autowired EntityManagerFactory emf;

    @Test
    void 프록시_초기화과정과_타입비교_테스트() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 1. [준비] 회원 생성 및 영속성 컨텍스트 초기화
            UniMember member = new UniMember();
            member.setUsername("JPA_PROXY_TEST");
            em.persist(member);

            em.flush();
            em.clear(); // 🧹 영속성 컨텍스트를 깨끗하게 비웁 (진짜 DB 조회를 유도)

            System.out.println("\n============== 1. 프록시 조회 (getReference) ==============");
            // find()와 달리, getReference()는 쿼리를 날리지 않고 '가짜 껍데기'만 줍니다.
            UniMember refMember = em.getReference(UniMember.class, member.getId());

            System.out.println("refMember Class = " + refMember.getClass().getName());

            System.out.println("\n============== 2. 초기화 전 상태 확인 ==============");
            // JPA 표준 유틸리티로 이 객체가 프록시인지, 초기화되었는지 확인 가능
            boolean isLoadedBefore = emf.getPersistenceUnitUtil().isLoaded(refMember);
            System.out.println("isLoaded(초기화 전) = " + isLoadedBefore);

            System.out.println("\n============== 3. 강제 초기화 (메서드 호출) ==============");
            // ✨ 이 시점에 DB 쿼리가 나갑니다! (getName 호출 시 진짜 데이터가 필요하니까)
            System.out.println("member.getName() 호출 결과 = " + refMember.getUsername());

            boolean isLoadedAfter = emf.getPersistenceUnitUtil().isLoaded(refMember);
            System.out.println("isLoaded(초기화 후) = " + isLoadedAfter);

            System.out.println("\n============== 4. 초기화 후 클래스 확인 ==============");
            // 가장 많이 오해하는 부분! 초기화 했다고 해서 껍데기가 원본 클래스로 변할까?
            System.out.println("refMember Class = " + refMember.getClass().getName());

            System.out.println("\n============== 5. 타입 비교 (== vs instanceof) ==============");
            // ☠️ [함정] == 비교
            boolean isSameClass = (refMember.getClass() == UniMember.class);
            System.out.println("refMember.getClass() == UniMember.class : " + isSameClass);

            // ✅ [정석] instanceof 비교
            boolean isInstanceOf = (refMember instanceof UniMember);
            System.out.println("refMember instanceof UniMember : " + isInstanceOf);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}