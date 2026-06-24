package ding.co.hellojpa.week3;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Week3ManyToManyTest {

    @Autowired
    EntityManagerFactory emf;

    @Test
    void 다대다_연결엔티티_승격_테스트() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 1. 회원 및 상품 저장
            ManyMember member = new ManyMember();
            member.setName("VIP회원");
            em.persist(member);

            ManyProduct product = new ManyProduct();
            product.setName("맥북 프로");
            em.persist(product);

            // 2. 회원이 상품을 주문함 (연결 엔티티 생성)
            MemberProduct orderItem = new MemberProduct();

            // 수량, 날짜 등의 추가 비즈니스 데이터를 완벽하게 관리할 수 있음!
            orderItem.setMemberAndProduct(member, product, 2);
            em.persist(orderItem);

            tx.commit();
            System.out.println(">>> 성공! 중간 엔티티를 통해 안전하게 N:M 관계가 저장되었습니다.");

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}