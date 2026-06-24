package ding.co.hellojpa.week3;

import jakarta.persistence.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Week3ManyToManyToxicTest {

    @Autowired
    EntityManagerFactory emf;

    @Test
    void 다대다_편리함의_함정과_한계_체험하기() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            System.out.println("=== 1. 회원과 상품 객체 생성 ===");
            ToxicProduct product = new ToxicProduct();
            product.setName("아이패드 프로");
            em.persist(product);

            ToxicMember member = new ToxicMember();
            member.setName("편리함에_속은_회원");

            System.out.println("\n=== 2. 주문 발생 (단순히 리스트에 add) ===");
            // "회원이 상품을 샀다" -> 그냥 리스트에 add 하면 끝! 너무 편하죠?
            member.getProducts().add(product);
            em.persist(member);

            // 강제로 DB에 쿼리를 밀어내서 확인해봅시다.
            System.out.println("\n=== 3. DB에 쿼리 전송 (마법 발생) ===");
            em.flush();
            em.clear();

            // ---------------------------------------------------------
            // [기획자의 등장] 여기서부터 비극이 시작됩니다.
            // ---------------------------------------------------------
            System.out.println("\n=== 4. 한계점 도착 (데이터 추가 불가) ===");
            System.out.println("기획자: '개발자님, 아이패드 몇 개(수량) 샀는지랑, 언제 샀는지도 화면에 띄워주세요.'");
            System.out.println("개발자: '어...? 수량이랑 날짜를 넣을 필드가 없는데요...?'");

            // member.getProducts() 에는 오직 ToxicProduct 객체만 들어있습니다.
            // 중간 테이블(TOXIC_MEMBER_PRODUCT)은 JPA가 숨겨놓았기 때문에
            // 자바 코드에서 접근해서 컬럼을 추가할 방법이 아예 없습니다!

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}