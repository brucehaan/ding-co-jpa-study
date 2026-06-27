package ding.co.hellojpa.week4;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static ding.co.hellojpa.week4.PaymentStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
public class Week4PaymentTest {
    @Autowired
    EntityManagerFactory emf;

    @Test
    void 결제도메인_상속매핑_및_컨버터_동작_테스트() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            log.info("1. 카드 결제 데이터 생성 및 저장");
            CardPayment cardPay = new CardPayment();
            cardPay.setAmount(50000);
            cardPay.setStatus(APPROVED);
            cardPay.setCardCompany("신한카드");

            // 개발자는 평문(원래 카드번호)으로 편하게 세팅한다
            String plainCardNumber = "1234-5678-9012-3456";
            cardPay.setCardNumber(plainCardNumber);

            em.persist(cardPay); // 저장 (이 순간 Converter가 개입하여 암호화 적용)

            // 쿼리를 DB로 강제로 날리고, 1차 캐시를 비워 DB에서 직접 다시 읽어오도록 세팅
            em.flush();
            em.clear();

            log.info("2. [DBA 시점] DB에 진짜 암호화되어 들어갔는지 확인");
            String nativeSql = "SELECT CARD_NUMBER FROM CARD_PAYMENT WHERE PAYMENT_ID = " + cardPay.getId();
            String dbRawData = (String) em.createNativeQuery(nativeSql).getSingleResult();

            log.info("DB에 저장된 실제 카드번호 : " + dbRawData);
            assertThat(dbRawData).isEqualTo("ENCRYPTED_1234-5678-9012-3456"); // 암호화 확인

            log.info("3. [개발자 시점] 부모 타입(payment)으로 다형성 조회");
            Payment findPay = em.find(Payment.class, cardPay.getId());
            assertThat(findPay).isInstanceOf(CardPayment.class);

            CardPayment findCardPay = (CardPayment) findPay;
            log.info("자바 객체로 조회된 카드번호: {}", findCardPay.getCardNumber());
            assertThat(findCardPay.getCardNumber()).isEqualTo(plainCardNumber);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
    }
}
