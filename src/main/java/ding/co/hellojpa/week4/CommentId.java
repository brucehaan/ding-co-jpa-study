package ding.co.hellojpa.week4;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CommentId implements Serializable {
    private Long postId; // 부모 키를 받을 공간 (필드명 중요)
    private Long commentId; // 내(자식) ID
}
