package ding.co.hellojpa.week3;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class OrphanParent {
    @Id @GeneratedValue
    private Long id;
    private String name;

    // ✨ 핵심: 두 가지 옵션을 모두 켬!
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrphanChild> children = new ArrayList<>();

    // 편의 메서드
    public void addChild(OrphanChild child) {
        children.add(child);
        child.setParent(this);
    }
}