package ding.co.hellojpa.week4;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode // 필수, 키 값이 같으면 같은 객체로 인식해야 함
public class SlotId implements Serializable {
    private String charId; // Entity의 필드명과 똑같아야 함
    private int slotNum;
}
