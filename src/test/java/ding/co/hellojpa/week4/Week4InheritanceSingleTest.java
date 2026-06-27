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
public class Week4InheritanceSingleTest {
    @Autowired
    EntityManagerFactory emf;

    @Test
    void 단일테이블전략_저장및조회_테스트() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            log.info("1. 저장 (insert) 시도");
            SingleMovie movie = new SingleMovie();
            movie.setName("인셉션"); // 부모 공통 속성
            movie.setPrice(15000); // 부모 공통 속성
            movie.setDirector("크리스토퍼 놀란"); // 영화 전용 속성
            movie.setActor("레오나르도 디카프리오"); // 영화 전용 속성

            // 쿼리가 몇 번 나가는지 확인
            em.persist(movie);

            em.flush();
            em.clear();

            log.info("2. 조회(select) 시도");
            // 조인(join) 쿼리가 나가는지 확인
            SingleMovie findMovie = em.find(SingleMovie.class, movie.getId());

            log.info("조회된 영화 이름 : {}", findMovie.getName());
            log.info("조회된 감독 이름 : {}", findMovie.getDirector());

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
    }
}
