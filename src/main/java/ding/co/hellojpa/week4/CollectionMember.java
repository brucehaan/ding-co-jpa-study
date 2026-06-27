package ding.co.hellojpa.week4;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter
public class CollectionMember {
    @Id @GeneratedValue
    private Long id;
    private String name;

    // 마법 시작 : 이건 단순한 값들이 모인 컬렉션이야.
//    @ElementCollection
//    @CollectionTable(
//            name = "FAVORITE_FOOD", // 별도의 테이블 이름 지정
//            joinColumns = @JoinColumn(name = "MEMBER_ID") // 연결고리 (FK) 지정
//    )
//    @Column(name = "FOOD_NAME") // 값이 하나(String)니까 컬럼명 지정

    // 핵심 : mappedBy를 써서 "나는 리스트만 볼 뿐, fk 관리는 자식(member필드)이 한다"고 선언
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FavoriteFoodEntity> favoriteFoods = new ArrayList<>();

    // 연관관계 편의 메서드 (데이터를 넣을 때 자식에게도 내 정보를 줌)
    public void addFavoriteFood(FavoriteFoodEntity food) {
        this.favoriteFoods.add(food);
        food.setMember(this); // 자식의 fk값 세팅
    }
}
