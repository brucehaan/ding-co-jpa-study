package ding.co.hellojpa.week5;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuerydslConfig {
    @PersistenceContext
    private EntityManager em;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        // 이 Factory를 빈으로 등록하면, 어디서든 @Autowired나 @RequiredArgsConstructor로 주입받아 쓸 수 있다.
        return new JPAQueryFactory(em);
    }
}
