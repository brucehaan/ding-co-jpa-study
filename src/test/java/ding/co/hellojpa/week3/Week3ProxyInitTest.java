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
public class Week3ProxyInitTest {
    @Autowired
    EntityManagerFactory emf;

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
            em.clear(); // 영속성 컨텍스트를 깨끗하게 비움 (진짜 db 조회를 유도)

            log.info("1. 프록시 조회( getReference) ");
            // find()와 달리, getReference()는 쿼리를 날리지 않고 '가짜 껍데기'만 줌.
            UniMember refMember = em.getReference(UniMember.class, member.getId());
            log.info("refMember class = {}", refMember.getClass().getName());

            log.info("2. 초기화 전 상태 확인");
            // JPA 표준 유틸리티로 이 객체가 프록시인지, 초기화되었는지 확인 가능
            boolean isLoadedBefore = emf.getPersistenceUnitUtil().isLoaded(refMember);
            log.info("isLoaded(초기화 전) = {}", isLoadedBefore);

            log.info("3. 강제 초디화(메서드 호출)");
            // 이 시점에 db 쿼리가 나감 (getNAme 호출 시 진짜 데이터가 필요하니까)
            log.info("member.getName() 호출 결과 = {}", refMember.getUsername());

            boolean isLoadedAfter = emf.getPersistenceUnitUtil().isLoaded(refMember);
            log.info("isLoaded(초기화 후) = {}", isLoadedAfter);

            log.info("4. 초기화 후 클래스 확인");
            // 초기화 했다고 해서 껍데기가 원본 클래스로 변할까?
            log.info("refMember class = {}", refMember.getClass().getName());

            log.info("5. 타입 비교 vs instanceof");
            // 함정 : == 비교
            boolean isSameClass = refMember.getClass() == UniMember.class;
            log.info("refMember.getClass() == UniMember.class : {}", isSameClass);

            // 정석 : instanceof 비교
            boolean isInstanceOf = refMember instanceof UniMember;
            log.info("refMember instanceof UniMember : {}", isInstanceOf);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
    }
}
