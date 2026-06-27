package ding.co.hellojpa.week4;

import jakarta.persistence.Convert;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@DiscriminatorValue("CARD") // PAY_METHOD 컬럼에 'CARD'로 저장됨
public class CardPayment extends Payment {
    private String cardCompany; // 예 : 신한카드, 현대카드

    // 카드 번호는 무조건 암호화 컨버터를 거침
    @Convert(converter = CryptoConverter.class)
    private String cardNumber;
}
