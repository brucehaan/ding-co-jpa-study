package ding.co.hellojpa.week4;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Comment {
    @EmbeddedId
    private CommentId id;

    // 이 연관관계(FK)로 받아온 Post의 ID 값을,
    // 내 복합키 객체(CommentId) 안의 postId에 복사해줘
    @MapsId("postId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID")
    private Post post;

    private String content;
}
