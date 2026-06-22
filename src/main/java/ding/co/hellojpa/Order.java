package ding.co.hellojpa;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "ORDERS") // ORDER는 DB 예약어일 수 있으므로 보통 ORDERS로 씁니다.
@Getter @Setter
@EntityListeners(AuditingEntityListener.class) // Auditing 기능 적용
public class Order implements Persistable<String> {

    @Id
    private String id; // 수동으로 넣을 주문번호 (예: ORDER-2023-001)

    @CreatedDate
    private LocalDateTime createdDate; // 처음 저장될 때 시간이 자동 기록됨

    // Persistable 인터페이스 구현체 1: ID 반환
    @Override
    public String getId() {
        return id;
    }

    // ⭐ Persistable 인터페이스 구현체 2: 새로운 객체인지 판단하는 로직 오버라이딩
    @Override
    public boolean isNew() {
        // ID 값이 있더라도, '생성일자(createdDate)'가 아직 세팅되지 않았다면(null)
        // 무조건 새로운 객체로 판단하고 persist()를 호출해라!
        return createdDate == null;
    }
}