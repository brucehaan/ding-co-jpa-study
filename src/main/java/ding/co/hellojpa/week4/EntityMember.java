package ding.co.hellojpa.week4;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class EntityMember {
    @Id @GeneratedValue
    private Long id;
    private String name;


    /**
     * 값 타입 컬렉션 대신 일대다(1:N) 엔티티 관계로 승격
     * Cascade.ALL + orphanRemoval = true를 쓰면
     * 부모(Member)의 리스트에서 요소를 빼기만 해도 알아서 DB에서 DELETE쿼리가 나감
     * 즉, 엔티티로 만들었지만 마치 '값 타입 컬렉션'처럼 편하게 관리할 수 있음
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "MEMBER_ID")
    private List<FavoriteFoodEntity> favoriteFoods = new ArrayList<>();
}
