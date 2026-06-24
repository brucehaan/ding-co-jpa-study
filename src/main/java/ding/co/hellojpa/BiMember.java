package ding.co.hellojpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class BiMember {

    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    private String username;

    // ==========================================
    // 👑 연관관계의 주인 (외래 키 관리)
    // ==========================================
    // 외래 키(TEAM_ID)를 가지고 있는 곳이므로 연관관계의 주인입니다.
    // 여기에 값을 세팅해야만 DB에 정상적으로 반영됩니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    private BiTeam team;

    // ==========================================
    // 🤝 연관관계 편의 메서드 (매우 중요!)
    // ==========================================
    // 단순한 Setter가 아님을 강조하기 위해 changeTeam 으로 명명
    public void changeTeam(BiTeam team) {
        // 1. 기존에 이미 팀이 있었다면? -> 기존 팀의 리스트에서 나를 지워야 함! (유령 회원 방지)
        if (this.team != null) {
            this.team.getMembers().remove(this);
        }

        // 2. 주인에게 값 설정 (DB 저장용)
        this.team = team;

        // 3. 반대쪽(거울) 리스트에 추가 (순수 객체 상태 동기화용)
        if (team != null) {
            team.getMembers().add(this);
        }
    }


}