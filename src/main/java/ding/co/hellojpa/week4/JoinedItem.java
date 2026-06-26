package ding.co.hellojpa.week4;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Inheritance(strategy = InheritanceType.JOINED) // 조인 전략 선언
@DiscriminatorColumn(name = "DTYPE") // 구분 컬럼 생성 (기본값 : DTYPE)
public abstract class JoinedItem {
    // 부모 클래스는 단독으로 쓰일 일이 없으므로 추상(abstract) 클래스로 만드는 것을 권장
    @Id @GeneratedValue
    @Column(name = "ITEM_ID")
    private Long id;

    private String name;
    private int price;
}
