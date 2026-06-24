package ding.co.hellojpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class UniTeam {

    @Id @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long id;

    private String name;

    // 🚫 List<UniMember> members;  <-- 이런 거 없습니다!
    // 팀은 자기가 어떤 회원들을 데리고 있는지 모릅니다. (필드 자체가 없음)
    // 오직 회원이 팀을 참조할 뿐입니다.
}