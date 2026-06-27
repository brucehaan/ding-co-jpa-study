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
public class Week4SideEffectTest {
    @Autowired
    EntityManagerFactory emf;

    @Test
    void 객체공유_사이드이펙트_버그발생_테스트() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 1. 공통 주소 객체 생성
            // (주의 : Address 클래스에 Setter가 열려 있다고 가정하자)
            ImmutableAddress sharedAddress = new ImmutableAddress("서울", "강남", "10000");

            // 2. 회원 A 가입
            EmbeddedMember memberA = new EmbeddedMember();
            memberA.setName("회원A");
            memberA.setHomeAddress(sharedAddress); // 참조값 전달

            // 3. 회원 B 가입
            EmbeddedMember memberB = new EmbeddedMember();
            memberB.setName("회원B");
            memberB.setHomeAddress(sharedAddress); // 같은 객체를 공유함 (위험)
            em.persist(memberB);

            // 4. 회원 A만 이사감 (판교로 변경)
            // 개발자 의도 : 회원A의 주소만 바꿔야지
//            memberA.getHomeAddress().setCity("판교");
            // 대참사 발생 : sharedAddress 객체 자체의 내용물이 바뀜

            ImmutableAddress oldAddress = memberA.getHomeAddress();

            // 새로운 주소 객체를 통째로 만들어서 덮어씌움 (안전함)
            ImmutableAddress newAddress = new ImmutableAddress("판교", oldAddress.getStreet(), oldAddress.getZipcode());
            memberA.setHomeAddress(newAddress);

            em.flush();
            em.clear();

            // 5. DB에서 다시 조회하기
            EmbeddedMember findMemberA = em.find(EmbeddedMember.class, memberA.getId());
            EmbeddedMember findMemberB = em.find(EmbeddedMember.class, memberB.getId());

            log.info("회원 A의 주소 : {}", findMemberA.getHomeAddress().getCity());
            log.info("회원 B의 주소 : {}", findMemberB.getHomeAddress().getCity());

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
    }

    @Test
    void 객체_동일성_비교() {
        Address a = new Address("서울", "강남", "123");
        Address b = new Address("서울", "강남", "123");

        log.info("비교시작");
        // 동일성 비교 (==)
        log.info("a == b {}", a == b);
        // 결과 : false (new 를 두 번 했으니 메모리 주소가 다름)

        // 동등성 비교 (equals) - 재정의했을 때
        log.info("a.equals(b): {}", a.equals(b));
        // 결과 : true
    }
}
