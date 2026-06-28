package ding.co.hellojpa.week5;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
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
public class Week5SearchTest {
    @Autowired
    JPAQueryFactory queryFactory;

    @Autowired
    EntityManager em;

    @Test
    void 다양한_검색조건_테스트() {
        // given
        em.persist(new QuerydslMember("teamA_user", 10));
        em.persist(new QuerydslMember("teamB_user", 20));
        em.persist(new QuerydslMember("teamC_user", 30));

        // when
        QuerydslMember result = queryFactory
                .selectFrom(querydslMember)
                .where(
                        querydslMember.username.contains("team"), // like '%team%'
                        querydslMember.username.startsWith("team"), // like 'team%'
                        querydslMember.age.between(10, 30), // BETWEEN 10 and 30
                        querydslMember.age.in(10, 20), // in (10, 20)
                        querydslMember.age.goe(20) // >= 20
                )
                .fetchFirst();// limit1 (맨 처음 한 건만 가져옴)

        // then
        // 조건 : 이름에 'team' 포함 & 시작, 나이 10 ~ 30 사이, 나이 10 or 20, 나이 20이상
        // 결론적으로 조건을 모두 만족하는 건 'teamB_user(20살)' 뿐
        assertThat(result.getUsername()).isEqualTo("teamB_user");
        assertThat(result.getAge()).isEqualTo(20);
    }

    @Test
    void 정렬과_페이징_테스트() {
        // given (나이는 모두 100살로 동일)
        em.persist(new QuerydslMember(null, 100)); // 이름이 없는 회원
        em.persist(new QuerydslMember("member5", 100));
        em.persist(new QuerydslMember("member6", 100));
        em.persist(new QuerydslMember("member7", 100));

        // when 1 : 컨텐츠 조회 (페이징 + 정렬)
        List<QuerydslMember> content = queryFactory
                .selectFrom(querydslMember)
                .where(querydslMember.age.eq(100))
                .orderBy(
                        querydslMember.age.desc(),
                        querydslMember.username.asc().nullsLast()
                )
                .offset(1) // 0부터 시작이므로, 1은 두 번째 데이터부터 가져오라는 뜻(건너뛰기)
                .limit(2) // 최대 2건만 가져와라
                .fetch();

        // when 2 : Total Count 쿼리 (별도 분리 방식 - 최신 트렌드)
        Long totalCount = queryFactory
                .select(querydslMember.count()) // SQL : count(member_id)
                .from(querydslMember)
                .where(querydslMember.age.eq(100))
                .fetchOne();

        // then 검증
        assertThat(content.size()).isEqualTo(2); // limit2 적용 확인

        /*
        정렬 : member5, member6, member7, null 순서인데
        offset(1)로 member5를 건너뛰었으므로 6과 7이 나와야 함
         */
        assertThat(content.get(0).getUsername()).isEqualTo("member6");
        assertThat(content.get(1).getUsername()).isEqualTo("member7");

        assertThat(totalCount).isEqualTo(4L); // 전체 데이터 개수
    }

    @Test
    void 집합함수와_GroupBy_테스트() {
        // Given (초기 데이터 세팅)
        em.persist(new QuerydslMember("memberA", 10));
        em.persist(new QuerydslMember("memberB", 20));

        // when : 전체 회원의 수, 나이 합계, 나이 평균, 최대 나이 조회
        // 집합 함수들을 select에 콤마로 나열하면 결과가 Tuple 리스트로 나옴
        List<com.querydsl.core.Tuple> result = queryFactory
                .select(
                        querydslMember.count(),
                        querydslMember.age.sum(),
                        querydslMember.age.avg(),
                        querydslMember.age.max()
                )
                .from(querydslMember)
                .fetch();

        Tuple tuple = result.get(0);

        // then : 꺼낼 때는 select에서 작성했던 코드를 그대로 get()안에 넣으면 됨
        assertThat(tuple.get(querydslMember.count())).isEqualTo(2L); // 총 2명
        assertThat(tuple.get(querydslMember.age.sum())).isEqualTo(30); // 10 + 20
        assertThat(tuple.get(querydslMember.age.avg())).isEqualTo(15.0); // 평균 15
        assertThat(tuple.get(querydslMember.age.max())).isEqualTo(20); // 최대 20
    }

    @Test
    void paging_테스트() {
        // given (데이터 4개 생성)
        em.persist(new QuerydslMember("member1", 10));
        em.persist(new QuerydslMember("member2", 20));
        em.persist(new QuerydslMember("member3", 30));
        em.persist(new QuerydslMember("member4", 40));

        em.flush();
        em.clear();

        // when 1 : 컨텐츠 조회 (데이터만 딱 2개 가져옴)
        List<QuerydslMember> content = queryFactory
                .selectFrom(querydslMember)
                .orderBy(querydslMember.username.desc())
                .offset(0) // 0부터 시작이므로 첫 번째 데이터부터 (skip 0)
                .limit(2)
                .fetch();

        // when 2 : 카운트 쿼리 (별도로 명시적 작성)
        // fetchCount() 대신 select(엔티티.count()) 사용이 최신 권장 방식
        Long totalCount = queryFactory
                .select(querydslMember.count()) // SQL : select count(member_id)
                .from(querydslMember)
                .fetchOne();

        // then
        assertThat(content.size()).isEqualTo(2);
        assertThat(content.get(0).getUsername()).isEqualTo("member4");
        assertThat(content.get(1).getUsername()).isEqualTo("member3");
        assertThat(totalCount).isEqualTo(4L);
    }

    @Test
    void aggregation() {
        em.persist(new QuerydslMember("member1", 10));
        em.persist(new QuerydslMember("member2", 20));
        em.persist(new QuerydslMember("member3", 30));
        em.persist(new QuerydslMember("member4", 20));
        em.persist(new QuerydslMember("member5", 20));

        List<Tuple> result = queryFactory
                .select(
                        querydslMember.count(),
                        querydslMember.age.sum(),
                        querydslMember.age.avg(),
                        querydslMember.age.max(),
                        querydslMember.age.min()
                )
                .from(querydslMember)
                .fetch();
        Tuple tuple = result.get(0);

        // 조회할 때 썼던 표현식을 그대로 넣어서 꺼냄
        assertThat(tuple.get(querydslMember.count())).isEqualTo(5);
        assertThat(tuple.get(querydslMember.age.sum())).isEqualTo(100);
    }
}

