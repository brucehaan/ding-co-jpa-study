package ding.co.hellojpa.week3;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class ManyMember {
    @Id @GeneratedValue
    private Long id;
    private String name;

    // 회원은 이제 상품이 아니라 주문내역(연결 엔티티) 리스트를 갖는다.
    @OneToMany(mappedBy = "member")
    private List<MemberProduct> orderHistories = new ArrayList<>();
}
