package ding.co.hellojpa.week4;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class OverrideMember {
    @Id @GeneratedValue
    private Long id;

    // 1. 집 주소 : 원래 정의된 컬럼명 (city, street, zipcode) 그대로 사용
    @Embedded
    private Address homeAddress;

    // 2. 회사 주소 : db 컬럼이 겹치지 않게 이름을 WORK_~로 재정의
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "city", column = @Column(name = "WORK_CITY")),
            @AttributeOverride(name = "street", column = @Column(name = "WORK_STREET")),
            @AttributeOverride(name = "zipcode", column = @Column(name = "WORK_ZIPCODE"))
    })
    private Address workAddress;
}
