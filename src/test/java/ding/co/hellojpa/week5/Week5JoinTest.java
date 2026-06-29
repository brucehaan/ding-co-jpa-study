package ding.co.hellojpa.week5;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ding.co.hellojpa.week4.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static ding.co.hellojpa.week5.QQuerydslMember.querydslMember;
import static ding.co.hellojpa.week5.QQuerydslTeam.querydslTeam;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Transactional
public class Week5JoinTest {
    @Autowired
    JPAQueryFactory queryFactory;
    @Autowired
    EntityManager em;

    @PersistenceUnit
    EntityManagerFactory emf; // 패치 조인 로딩 여부 검증용

    @BeforeEach
    void 데이터_세팅() {
        QuerydslTeam teamA = new QuerydslTeam("teamA");
        QuerydslTeam teamB = new QuerydslTeam("teamB");
        em.persist(teamA);
        em.persist(teamB);

        QuerydslMember member1 = new QuerydslMember("member1", 10);
        member1.setTeam(teamA);
        em.persist(member1);

        QuerydslMember member2 = new QuerydslMember("teamB", 20); // 이름이 팀 이름과 같음 (세타 조인용)
        member2.setTeam(teamB);
        em.persist(member2);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("1. 기본 조인 : 팀 A에 소속된 모든 회원 찾기")
    void basicJoin() {
        List<QuerydslMember> result = queryFactory
                .selectFrom(querydslMember)
                .join(querydslMember.team, querydslTeam) // member.team과 team을 조인
                .where(querydslTeam.name.eq("teamA"))
                .fetch();

        assertThat(result).extracting("username").containsExactly("member1");
    }

    @Test
    @DisplayName("2. 세타 조인 : 회원의 이름이 팀 이름과 똑같은 회원 찾기 (연관관계 X)")
    void thetaJoin() {
        List<QuerydslMember> result = queryFactory
                .select(querydslMember)
                .from(querydslMember, querydslTeam) // from 절에 엔티티 2개 나열
                .where(querydslMember.username.eq(querydslTeam.name))
                .fetch();
        assertThat(result.get(0).getUsername()).isEqualTo("teamB");
    }

    @Test
    @DisplayName("3. ON절 필터링 : 회원과 팀을 조인하되, 팀 이름이 'teamA'인 팀만 조인 (회원은 다 가져옴)")
    void joinOnFiltering() {
        // Tuple로 반환됨 (회원 객체, 팀 객체 두 개를 각각 뽑아오므로)
        List<Tuple> result = queryFactory
                .select(querydslMember, querydslTeam)
                .from(querydslMember)
                .leftJoin(querydslMember.team, querydslTeam)
                .on(querydslTeam.name.eq("teamA")) // 외부 조인 대상 필터링
                .fetch();
        for (Tuple tuple : result) {
            log.info("tuple: {}", tuple);
        }
    }

    @Test
    @DisplayName("4. 페치 조인 (N+1 문제 해결 확인)")
    void fetchJoinTest() {
        // 페치 조인 미적용 시
        QuerydslMember findMemberNoFetch = queryFactory
                .selectFrom(querydslMember)
                .where(querydslMember.username.eq("member1"))
                .fetchOne();

        // Team 객체가 초기화되었는지 (DB에서 가져왔는지) 확인 -> false
        boolean isLoadedNoFetch = emf.getPersistenceUnitUtil().isLoaded(findMemberNoFetch.getTeam());
        assertThat(isLoadedNoFetch).as("페치 조인 미적용 시 프록시 상태여야 함").isFalse();

        em.clear();

        // 페치 조인 적용 시
        QuerydslMember findMemberFetch = queryFactory
                .selectFrom(querydslMember)
                .join(querydslMember.team, querydslTeam).fetchJoin()
                .where(querydslMember.username.eq("member1"))
                .fetchOne();

        // Team 객체가 초기화되었는지 확인
        boolean isLoadedFetch = emf.getPersistenceUnitUtil().isLoaded(findMemberFetch.getTeam());
        assertThat(isLoadedFetch).as("페치 조인 적용 시 이미 로딩되어 있어야 함").isTrue();
    }

    /*
    세타 조인 : 회원의 이름이 팀 이름과 같은 회원 조회
     */
    @Test
    void theta_join() {
        em.persist(new QuerydslMember("teamA"));
        em.persist(new QuerydslMember("teamB"));

        List<QuerydslMember> result = queryFactory
                .select(querydslMember)
                .from(querydslMember, querydslTeam)
                .where(querydslMember.username.eq(querydslTeam.name)) // where 절에서 매칭
                .fetch();
        assertThat(result.get(0).getUsername()).isEqualTo("teamB");
    }
}
