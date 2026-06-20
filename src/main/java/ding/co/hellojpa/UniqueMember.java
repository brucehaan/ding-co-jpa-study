package ding.co.hellojpa;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class UniqueMember {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ⭐️ 핵심! DB 차원에서 중복을 막는 제약조건 설정
    @Column(unique = true)
    private String name;

    public UniqueMember(String name) {
        this.name = name;
    }
}