package ding.co.hellojpa.week3;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class UniTeam {
    @Id @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long id;

    private String name;

    /*
    팀은 자기가 어떤 회원들을 데리고 있는지 모른다. (필드 자체가 없음)
    오직 회원이 팀을 참조할 뿐
     */
}
