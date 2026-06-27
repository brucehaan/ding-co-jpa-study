package ding.co.hellojpa.week4;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Post {
    @Id @GeneratedValue
    @Column(name = "POST_ID")
    private Long id;
    private String title;
}
