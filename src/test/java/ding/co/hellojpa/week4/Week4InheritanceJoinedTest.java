package ding.co.hellojpa.week4;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.query.Param;

@Slf4j
@SpringBootTest
public class Week4InheritanceJoinedTest {
    @Autowired
    EntityManagerFactory emf;

    @Test
    void 조인전략_저장및조회_테스트() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            log.info("1. 저장 (INSERT) 시도 ");
            JoinedMovie movie = new JoinedMovie();
            movie.setName("반지의 젭왕"); // 부모 속성
            movie.setPrice(10000); // 부모 속성
            movie.setDirector("피터 잭슨"); // 자식 속성
            movie.setActor("일라이저 우드"); // 자식 속성

            // JPA야, 영화 객체 하나 저장해줘
            em.persist(movie);

            // 쿼리를 db에 강제로 밀어내고 영속성 컨텍스트 비우기
            em.flush();
            em.clear();

            log.info("2. 조회 (select) 시도");
            // 1차 캐시가 비워졌으므로 db에서 직접 가져옴
            JoinedMovie findMovie = em.find(JoinedMovie.class, movie.getId());

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
