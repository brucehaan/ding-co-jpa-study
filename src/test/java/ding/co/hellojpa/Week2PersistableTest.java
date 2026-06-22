package ding.co.hellojpa;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class Week2PersistableTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    EntityManager em;

    @Test
    void Persistable_구현으로_SELECT쿼리_방어하기() {
        Order order = new Order();
        // 🚨 중요: ID를 수동으로 넣었습니다! (@GeneratedValue 아님)
        order.setId("ORDER-12345");

        System.out.println("=== 1. save() 호출 전 ===");

        // 이 시점에 order.getCreatedDate() 는 null 입니다.
        // 따라서 isNew()는 true를 반환하고, JPA는 "아, 이건 완전 새 거구나!" 하고
        // DB를 조회(SELECT)하지 않고 바로 persist()를 실행합니다.
        orderRepository.save(order);

        System.out.println("=== 2. save() 호출 후 ===");

        // 강제로 플러시를 날려서 INSERT 쿼리 확인
        System.out.println("=== 3. 플러시(flush) 실행 ===");
        em.flush();

        // 검증: 저장된 생성일자가 null이 아님을 확인
        System.out.println("생성일자 세팅 확인: " + order.getCreatedDate());
    }
}