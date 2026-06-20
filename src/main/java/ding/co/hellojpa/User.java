package ding.co.hellojpa;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // 1. "나는 DB 테이블이다!" (JPA 관리 대상)
@Data   // 2. 롬복: Getter, Setter, toString 자동 생성
@NoArgsConstructor // 3. JPA 필수: 기본 생성자 (리플렉션용)
@Table(name = "users") // H2에서 User라는 예약어가 있음
public class User {

    @Id // 4. PK (Primary Key)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 5. 번호는 DB가 알아서(Auto Increment)
    private Long id;

    private String name;

    // 테스트 코드에서 편하게 쓰려고 만든 생성자
    public User(String name) {
        this.name = name;
    }
}