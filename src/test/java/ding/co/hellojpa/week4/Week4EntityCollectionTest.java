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
public class Week4EntityCollectionTest {
    @Autowired
    EntityManagerFactory emf;

    @Test
    void 엔티티승격후_컬렉션수정_최적화_테스트() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            // 1. 데이터 저장 (String 대신 Entity 객체를 생성해서 넣음)
            EntityMember member = new EntityMember();
            member.setName("김회원");
            member.getFavoriteFoods().add(new FavoriteFoodEntity("치킨"));
            member.getFavoriteFoods().add(new FavoriteFoodEntity("피자"));
            member.getFavoriteFoods().add(new FavoriteFoodEntity("족발"));

            em.persist(member);

            em.flush();
            em.clear();

            log.info("2. 데이터 수정 시작");
            EntityMember findMember = em.find(EntityMember.class, member.getId());

        /*
        "치킨"을 지우고 "양념치킨"을 넣는다.
        자바 8이상 문법 : 이름이 "치킨"인 객체를 리스트에서 찾아서 지움
         */
            findMember.getFavoriteFoods().removeIf(food -> food.getFoodName().equals("치킨"));

            // 새 엔티티 추가
            findMember.getFavoriteFoods().add(new FavoriteFoodEntity("양념치킨"));

            log.info("3. 커밋 시점의 쿼리를 확인");
            tx.commit();

            // 이 순간 orphanRemoval = true 덕분에 리스트에서 빠진 '치킨'엔티티에 대한 delete 쿼리가 나감
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

    }
}
