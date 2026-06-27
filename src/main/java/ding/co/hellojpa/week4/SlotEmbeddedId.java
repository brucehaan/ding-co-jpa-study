package ding.co.hellojpa.week4;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable // 나는 엔티티 안에 내장될 수 있어
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SlotEmbeddedId implements Serializable {
    @Column(name = "CHAR_ID")
    private String charId;

    @Column(name = "SLOT_NUM")
    private int slotNum;
}
