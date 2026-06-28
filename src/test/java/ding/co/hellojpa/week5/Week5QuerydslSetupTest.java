package ding.co.hellojpa.week5;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static ding.co.hellojpa.week5.QQuerydslMember.querydslMember;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Transactional
public class Week5QuerydslSetupTest {

    @Autowired
    JPAQueryFactory queryFactory;

    @Autowired
    EntityManager em;

    @Test
    void QueryDSL_환경설정_및_동작_테스트() {
        // 1. [준비] 데이터 저장
        QuerydslMember member = new QuerydslMember("hello_querydsl");
        em.persist(member);

        em.flush();
        em.clear(); // 영속성 컨텍스트 깔끔하게 비우기

        // 2. [실행] QueryDSL로 쿼리 작성
        // static import 덕분에 QQuerydslMember.querydslMember 대신 querydslMember로 짧게 사용 가능
        QuerydslMember findMember = queryFactory
                .select(querydslMember)
                .from(querydslMember)
                .where(querydslMember.username.eq("hello_querydsl"))
                .fetchOne();

        // 3. 검증
        assertThat(findMember).isNotNull();
        assertThat(findMember.getUsername()).isEqualTo("hello_querydsl");
        assertThat(findMember.getId()).isEqualTo(member.getId());

        log.info("QueryDSL 설정 성공! 쿼리가 정상적으로 실행됨");
    }
}
