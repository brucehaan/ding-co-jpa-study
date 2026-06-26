package ding.co.hellojpa.week3;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class MemberProduct { // 실무에서는 orderitem, orders 등으로 명명
    @Id @GeneratedValue
    @Column(name = "ORDER_ITEM_ID")
    private Long id; // 비즈니스 의미가 없는 대리 키(Surrogate Key)

    // 회원족 매핑 (N : 1) - 주인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private ManyMember member;

    // 상품쪽 매핑 (N : 1) - 주인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    private ManyProduct product;

    // 핵심: 중간 테이블이 노출되었으므로 추가 정보를 마음껏 넣을 수 있다
    private int orderCount; // 주문 수량
    private LocalDateTime orderDate; // 주문 시간

    // 편의 메서드
    public void setMemberAndProduct(ManyMember member, ManyProduct product, int count) {
        this.member = member;
        this.product = product;
        this.orderCount = count;
        this.orderDate = LocalDateTime.now();
        member.getOrderHistories().add(this);
    }
}
