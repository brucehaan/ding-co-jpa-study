package ding.co.hellojpa.week4;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
// 핵심1 : 전략을 SINGLE_TABLE로 변경 (단일 테이블 전략)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
// 핵심2 : 단일 테이블에서는 DTYPE이 없으면
// 무슨 자식 데이터인지 알 수 없으므로 무조건 자동 생성됩니다. (생략해도 들어감)
@DiscriminatorColumn(name = "DTYPE")
@Getter @Setter
public abstract class SingleItem {
    @Id @GeneratedValue
    @Column(name = "ITEM_ID")
    private Long id;

    private String name;
    private int price;
}
