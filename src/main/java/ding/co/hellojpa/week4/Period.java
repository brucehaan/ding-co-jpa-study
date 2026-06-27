package ding.co.hellojpa.week4;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Embeddable // 나는 다른 엔티티 안에 내장될 수 있는 값 타입이다
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Period {
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // 핵심 장점 : 값 타입 안에 비즈니스 로직을 넣어 캡슐화 (응집도 향상)
    public boolean isValid() {
        return startDate != null && endDate != null && startDate.isBefore(endDate);
    }

}
