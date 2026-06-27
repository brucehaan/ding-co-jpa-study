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
public class Week4CollectionTest {
    @Autowired
    EntityManagerFactory emf;

    @Test
    void 값타입컬렉션_치명적단점_테스트() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 1. 데이터 저장
            CollectionMember member = new CollectionMember();
            member.setName("김회원");
            member.getFavoriteFoods().add(new FavoriteFoodEntity("치킨"));
            member.getFavoriteFoods().add(new FavoriteFoodEntity("피자"));
            member.getFavoriteFoods().add(new FavoriteFoodEntity("햄버거"));

            em.persist(member);

            em.flush();
            em.clear(); // DB 전송 후 영속성 컨텍스트 비우기

            log.info("2. 데이터 수정 시작");
            CollectionMember findMember = em.find(CollectionMember.class, member.getId());

            /*
            치킨을 지우고 양념치킨을 넣는다.
            개발자 예상: 치킨 하나만 delete되고, 양념치킨 하나만 insert 되겠지?
             */
            findMember.getFavoriteFoods().remove("치킨");
            findMember.getFavoriteFoods().add(new FavoriteFoodEntity("양념치킨"));

            log.info("3. 커밋 시점의 쿼리를 확인해라");
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
    }
}
