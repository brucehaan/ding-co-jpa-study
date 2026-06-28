package ding.co.hellojpa.week5;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
public class QuerydslMember {
    @Id @GeneratedValue
    private Long id;
    private String username;
    private int age;

    public QuerydslMember(String username, int age) {
        this.username = username;
        this.age = age;
    }
}
