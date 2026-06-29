package ding.co.hellojpa.week5;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuerydslMember {
    @Id @GeneratedValue
    private Long id;

    private String username;
    private int age;

    /*
    핵심 : Team과의 다대일(N:1) 양방향 연관관계 추가
    실무에서는 무조건 LAZY(지연 로딩)로 설정해야 N+1 문제를 방어할 수 있음
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    private QuerydslTeam team;

    public QuerydslMember(String username, int age) {
        this.username = username;
        this.age = age;
    }

    // 이름만 받는 생성자 (세타 조인 실습용)
    public QuerydslMember(String username) {
        this.username = username;
    }

    // 연관관계 편의 메서드(원래는 양쪽에 다 걸어줘야 하지만,
    // 이번 실습은 조회 위주라 단방향 세팅만 함)
    public void setTeam(QuerydslTeam team) {
        this.team = team;
    }
}
