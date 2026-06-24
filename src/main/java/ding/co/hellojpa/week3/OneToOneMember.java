package ding.co.hellojpa.week3;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// 주 테이블 (외래 키 보유 - 연관관계의 주인)
@Entity
@Getter
@Setter
public class OneToOneMember {
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOCKER_ID") // 유니크 제약조건 추가됨
    private Locker locker;
}