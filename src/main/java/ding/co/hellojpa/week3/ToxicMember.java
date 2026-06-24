package ding.co.hellojpa.week3;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class ToxicMember {

    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;
    private String name;

    // 와! 중간 엔티티(Order)를 안 만들었는데 바로 상품 리스트를 가질 수 있다니!
    @ManyToMany
    @JoinTable(name = "TOXIC_MEMBER_PRODUCT", // JPA가 몰래 만들어줄 중간 테이블 이름
            joinColumns = @JoinColumn(name = "MEMBER_ID"),
            inverseJoinColumns = @JoinColumn(name = "PRODUCT_ID")
    )
    private List<ToxicProduct> products = new ArrayList<>();
}