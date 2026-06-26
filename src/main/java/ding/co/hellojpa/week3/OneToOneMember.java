package ding.co.hellojpa.week3;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class OneToOneMember {
    @Id @GeneratedValue
    private Long id;
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOCKER_ID")
    private Locker locker;
}
