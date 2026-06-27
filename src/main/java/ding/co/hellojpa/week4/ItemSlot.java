package ding.co.hellojpa.week4;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Getter;
import lombok.Setter;

@Entity
@IdClass(SlotId.class)
@Getter @Setter
public class ItemSlot {
    @Id
    @Column(name = "CHAR_ID")
    private String charId;

    @Id
    @Column(name = "SLOT_NUM")
    private int slotNum;

    private String itemName;
}
