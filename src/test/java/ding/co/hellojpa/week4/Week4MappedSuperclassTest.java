package ding.co.hellojpa.week4;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class Week4MappedSuperclassTest {
    @Autowired
    EntityManagerFactory emf;

    @Test
    void 매핑정보상속_및_자동날짜기입_테스트() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            log.info("1. 회원 저장");
            MappedMember member = new MappedMember();
            member.setName("신규회원");

            // 우리는 날짜를 세팅한 적이 없다!
            em.persist(member);

            em.flush();
            em.clear();

            log.info("2. 저장된 날짜 확인");
            MappedMember findMember1 = em.find(MappedMember.class, member.getId());
            log.info("생성일자 : {}", findMember1.getCreatedDate());
            log.info("수정일자 : {}", findMember1.getLastModifiedDate());

            log.info("3. 회원 이름 수정 (시간 차이를 위해 1초 대기) ");
            Thread.sleep(1000);
            findMember1.setName("수정된 회원");

            em.flush();
            em.clear();

            log.info("4. 수정된 날짜 확인");
            MappedMember findMember2 = em.find(MappedMember.class, member.getId());
            log.info("생성일자 (변경안됨) : {}", findMember2.getCreatedDate());
            log.info("수정일자 (변경됨) : {}", findMember2.getLastModifiedDate());

            tx.commit();
        } catch (InterruptedException e) {
            tx.rollback();
            throw new RuntimeException(e);
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

    }
}
