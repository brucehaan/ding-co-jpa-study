package ding.co.hellojpa.week5;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static ding.co.hellojpa.week5.QQuerydslMember.querydslMember;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Transactional
public class Week5AdvancedQueryTest {
    @Autowired
    JPAQueryFactory queryFactory;
    @Autowired
    EntityManager em;

    @BeforeEach
    void 데이터_세팅() {
        em.persist(new QuerydslMember("member1", 10));
        em.persist(new QuerydslMember("member2", 20));
        em.persist(new QuerydslMember("member3", 30));
        em.persist(new QuerydslMember("member4", 40));
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("1. 서브 쿼리 : 나이가 가장 많은 회원 조회 (WHERE 절)")
    void subQueryInWhere() {
        QQuerydslMember memberSub = new QQuerydslMember("memberSub");

        QuerydslMember result = queryFactory
                .selectFrom(querydslMember)
                .where(querydslMember.age.eq(
                        JPAExpressions
                                .select(memberSub.age.max())
                                .from(memberSub)
                ))
                .fetchOne();
        assertThat(result.getAge()).isEqualTo(40);
        assertThat(result.getUsername()).isEqualTo("member4");
    }

    @Test
    @DisplayName("2. Case문 : 나이에 따라 등급 나누기")
    void caseBuilderTest() {
        List<String> result = queryFactory
                .select(new CaseBuilder()
                        .when(querydslMember.age.between(0, 20)).then("학생")
                        .when(querydslMember.age.between(21, 30)).then("청년")
                        .otherwise("어르신"))
                .from(querydslMember)
                .orderBy(querydslMember.age.asc())
                .fetch();

        assertThat(result).containsExactly("학생", "학생", "청년", "어르신");
    }

    @Test
    @DisplayName("3. 상수 추가 및 문자 더하기 (.stringValue)")
    void constantAndConcatTest() {
        Tuple constantResult = queryFactory
                .select(querydslMember.username, Expressions.constant("VIP"))
                .from(querydslMember)
                .fetchFirst();
        assertThat(constantResult.get(Expressions.constant("VIP"))).isEqualTo("VIP");

        String concatResult = queryFactory
                .select(querydslMember.username
                        .concat("_")
                        .concat(querydslMember.age.stringValue()))
                .from(querydslMember)
                .where(querydslMember.username.eq("member1"))
                .fetchOne();

        assertThat(concatResult).isEqualTo("member1_10");
    }

}
