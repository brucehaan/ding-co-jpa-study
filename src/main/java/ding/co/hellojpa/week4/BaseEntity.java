package ding.co.hellojpa.week4;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass // 난 엔티티가 아냐. 매핑 정보만 빌려줄게.
@EntityListeners(AuditingEntityListener.class) // 스프링 데이터 jpa가 날짜를 자동으로 넣어줌
@Getter
public abstract class BaseEntity { // 직접 생성할 일 없으므로 추상 클래스 권장
    @CreatedDate
    @Column(updatable = false) // 생성일은 한 번 들어가면 절대 수정되면 안 됨
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
