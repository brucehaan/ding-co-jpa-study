package ding.co.hellojpa.week3;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class OneToManyTeam {
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    // 팀이 주인! (내 테이블엔 없지만, 저쪽 테이블의 FK를 내가 관리하겠다)
    @OneToMany
    @JoinColumn(name = "TEAM_ID")
    private List<OneToManyMember> members = new ArrayList<>();
}