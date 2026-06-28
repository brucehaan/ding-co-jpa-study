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

    public QuerydslMember(String username) {
        this.username = username;
    }
}
