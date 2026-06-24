package ding.co.hellojpa.week3;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class ToxicProduct {

    @Id @GeneratedValue
    @Column(name = "PRODUCT_ID")
    private Long id;
    private String name;

    // 양방향이 필요하면 추가 (단순함 그 자체)
    @ManyToMany(mappedBy = "products")
    private List<ToxicMember> members = new ArrayList<>();
}