package ding.co.hellojpa.week3;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class TableMember {
    @Id @GeneratedValue
    private Long id;
    private String name;

    // Team 객체 자체가 아니라, DB의 FK인 '팀 아이디(숫자)'를 그대로 들고 있음
    private Long teamId;
}
