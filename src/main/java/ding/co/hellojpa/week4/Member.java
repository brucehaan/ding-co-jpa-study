package ding.co.hellojpa.week4;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Member {
    @Id @GeneratedValue
    private Long id;

    // 자바에서는 우아하게 boolean 을 쓴다.
    // 하지만 DB에 들어갈 때는 컨버터가 가로채서 "Y"나 "N"으로 바꿔치기한다.
    @Convert(converter = BooleanToYnConverter.class)
    private String isVip;
}
