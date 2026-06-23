package ding.co.hellojpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class JpaMember {

    @Id @GeneratedValue
    private Long id;

    @Column(name = "USERNAME")
    private String name;

    // ✨ 핵심!
    // 1. 객체는 Team 객체 그 자체를 참조합니다.
    // 2. @JoinColumn을 통해 DB의 FK(TEAM_ID) 컬럼과 매핑한다고 JPA에게 알려줍니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    private JpaTeam team;
}