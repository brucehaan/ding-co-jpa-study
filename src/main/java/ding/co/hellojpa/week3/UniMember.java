package ding.co.hellojpa.week3;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class UniMember {
    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    /**
     * 1. @ManyToOne : 관계의 종류 명시
     * - "저는 N(다)이고, 저 필드(UniTeam)는 1(일)입니다."
     * - (fetch = FetchType.LAZY) : 실무 필수 옵션
     * " 회원 조회할 때 팀은 가져오지 마. 나중에 필요하면 그때 조회할게. (성능 최적화)"
     */
    @ManyToOne(fetch = FetchType.LAZY)

    /**
     * 2. @JoinColumn : 외래 키 (FK) 매핑
     * - name = "TEAM_ID" : "DB의 UNI_MEMBER 테이블에 있는 'TEAM_ID'라는 컬럼과 연결해주세요."
     * - 생략하면 JPA가 알아서 이상한 이름(team_TEAM_ID)을 지어버리니 꼭 적어주세요.
     */
    @JoinColumn(name = "TEAM_ID")
    private UniTeam team;
}
