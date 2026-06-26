package ding.co.hellojpa.week3;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class BiTeam {
    @Id @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long id;

    private String name;

    /**
     * 양방향 매핑 설정 (가짜/거울)
     * mappedBy = "team" -> "나는 주인이 아닙니다. BiMember의 'team'필드가 진짜입니다."
     * 주의 : 여기에 값을 넣어도 DB의 외래 키(FK)는 변하지 않는다. 오직 조회용
     */
    @OneToMany(mappedBy = "team")
    private List<BiMember> members = new ArrayList<>(); // NullPointerException 방지를 위해 초기화
}
