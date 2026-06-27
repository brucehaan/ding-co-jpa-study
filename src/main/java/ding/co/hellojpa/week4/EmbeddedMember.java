package ding.co.hellojpa.week4;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class EmbeddedMember {
    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    private String name;

    @Embedded // 이 필드는 값 타입을 내장하고 있다
    private Period workPeriod;

    @Embedded
    private ImmutableAddress homeAddress;
}
