package ding.co.hellojpa;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class SequenceMember {

    @Id
    @SequenceGenerator(
            name = "SEQ_MEMBER_GEN",
            sequenceName = "SEQ_MEMBER_SEQ", // DB에 생성될 시퀀스 이름
            initialValue = 1,
            allocationSize = 50 // ✨ 핵심! 50개씩 미리 땡겨오기 (기본값)
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MEMBER_GEN")
    private Long id;

    private String name;

    public SequenceMember(String name) {
        this.name = name;
    }
}