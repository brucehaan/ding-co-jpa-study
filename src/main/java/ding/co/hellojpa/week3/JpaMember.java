package ding.co.hellojpa.week3;

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

    /**
     * 1. 객체는 Team 객체 그 자체를 참조한다.
     * 2. @JoinCollumn을 통해 DB의 FK(TEAM_ID) 컬럼과 매핑한다고 JPA에게 알려준다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    private JpaTeam team;
}
