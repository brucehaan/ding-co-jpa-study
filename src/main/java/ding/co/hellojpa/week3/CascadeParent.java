package ding.co.hellojpa.week3;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class CascadeParent {
    @Id @GeneratedValue
    private Long id;
    private String name;

    // ✨ 핵심 설정: cascade = CascadeType.ALL
    // 의미: 부모를 저장(persist)할 때 자식도 같이 저장해라!
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<CascadeChild> children = new ArrayList<>();

    // ✨ 연관관계 편의 메서드
    public void addChild(CascadeChild child) {
        children.add(child);
        child.setParent(this);
    }
}