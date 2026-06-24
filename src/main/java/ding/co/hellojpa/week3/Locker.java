package ding.co.hellojpa.week3;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

// 대상 테이블 (가짜/거울 - 양방향이 필요할 때만 추가)
@Entity
@Getter
@Setter
public class Locker {
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @OneToOne(mappedBy = "locker") // 주인이 아님
    private OneToOneMember member;
}
