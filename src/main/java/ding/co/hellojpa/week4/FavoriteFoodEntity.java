package ding.co.hellojpa.week4;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "FAVORITE_FOOD")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteFoodEntity {

    @Id @GeneratedValue
    private Long id; // 추적 가능해짐

    @Column(name = "FOOD_NAME")
    private String foodName;

    // 핵심 : 자식 쪽에서 외래 키(FK)를 직접 관리하도록 세팅
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private CollectionMember member;

    public FavoriteFoodEntity(String foodName) {
        this.foodName = foodName;
    }
}
