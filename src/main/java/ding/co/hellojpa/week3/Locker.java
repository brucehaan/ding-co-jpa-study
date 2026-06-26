package ding.co.hellojpa.week3;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Locker {
    @Id @GeneratedValue
    private Long id;
    private String name;

    @OneToOne(mappedBy = "locker") // 주인이 아님
    private OneToOneMember member;
}
