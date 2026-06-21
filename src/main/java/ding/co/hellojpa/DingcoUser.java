package ding.co.hellojpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "TB_USERS") // 1. 테이블 이름 지정 (안 하면 user 테이블 찾음)
public class DingcoUser {

    @Id
    private Long id;

    @Column(name = "user_name", nullable = false, length = 100)
    // 2. 컬럼 매핑: DB의 user_name 컬럼과 매핑 + NOT NULL + 길이 100
    private String username;

    private int age; // 생략하면 컬럼명 'age'로 자동 매핑
}