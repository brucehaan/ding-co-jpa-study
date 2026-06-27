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
public class Week4CompositeKeyTest {
    @Autowired
    EntityManagerFactory emf;

    @Test
    void 복합키_두가지_방식_비교_테스트() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            log.info("1. @IdClass 저장 및 조회");
            ItemSlot slot1 = new ItemSlot();
            slot1.setCharId("userA");
            slot1.setSlotNum(1);
            slot1.setItemName("전설의 검");
            em.persist(slot1);

            em.flush();
            em.clear();

            // 조회 : 식별자 클래스를 수동으로 생성해서 키로 넘김
            SlotId searchKey1 = new SlotId("userA", 1);
            ItemSlot findSlot1 = em.find(ItemSlot.class, searchKey1);
            log.info("@IdClass 조회 결과 : {}", findSlot1.getItemName());

            log.info("2. @EmbeddedId 저장 및 조회");
            ItemSlotV2 slot2 = new ItemSlotV2();

            // 저장 시에도 식별자 객체를 묶어서 세팅해야 함 (약간 귀찮음)
            SlotEmbeddedId searchKey2 = new SlotEmbeddedId("userB", 2);
            slot2.setId(searchKey2);
            slot2.setItemName("황금 방패");
            em.persist(slot2);

            em.flush();
            em.clear();

            // 조회 : 아까 만든 식별자 객체 그대로 사용
            ItemSlotV2 findSlot2 = em.find(ItemSlotV2.class, searchKey2);
            log.info("@EmbeddedId 조회 결과 {}", findSlot2.getItemName());

            // 데이터 접근 시 점(.)을 두 번 찍어야 하는 특징이 있음
            log.info("찾은 슬롯 번호 : {}", findSlot2.getId().getSlotNum());

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
    }
}
