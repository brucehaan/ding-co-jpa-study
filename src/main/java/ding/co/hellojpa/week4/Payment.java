package ding.co.hellojpa.week4;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Inheritance(strategy = InheritanceType.JOINED) // 정규화된 조인 전략
@DiscriminatorColumn(name = "PAY_METHOD") // DTYPE 이름을 직관적으로 변경
public class Payment {
    @Id @GeneratedValue
    @Column(name = "PAYMENT_ID")
    private Long id;

    private int amount; // 결제 금액

    // Enum 컨버터 사용 (DB엔 글자로, 자바에서는 Enum으로)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
}
