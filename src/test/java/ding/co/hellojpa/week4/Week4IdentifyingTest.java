package ding.co.hellojpa.week4;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class Week4IdentifyingTest {
    @Autowired
    EntityManagerFactory emf;

    @Test
    void 식별관계_MapsId_테스트() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 1. 부모(게시글) 저장
            Post post = new Post();
            post.setTitle("JPA 식별 관계란?");
            em.persist(post);
            // 이 시점에 post.getId()가 생성됨 (예: 1L)

            // 2. 자식(댓글) 저장
            Comment comment = new Comment();

            // CommentId를 만들 때 postId 값은 null로 둠 (JPA가 채워줄 거니까)
            CommentId commentId = new CommentId(null, 1L);
            comment.setId(commentId);

            // 연관관계 세팅
            comment.setPost(post);
            comment.setContent("어렵지만 재밌네오");

            // 이 순간 @MapsId가 동작하여 post.getId() 값을 commentId.postId에 복사
            em.persist(comment);

            em.flush();
            em.clear();

            log.info("조회 테스트");

            // 복합 키 객체를 만들어서 찾아야 함
            CommentId findKey = new CommentId(post.getId(), 1L);
            Comment findComment = em.find(Comment.class, findKey);

            log.info("댓글 내용 : {}", findComment.getContent());
            log.info("부모 게시글 제목 : {}", findComment.getPost().getTitle());

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

    }
}
