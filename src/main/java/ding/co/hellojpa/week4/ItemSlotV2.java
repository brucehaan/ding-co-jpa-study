package ding.co.hellojpa.week4;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class ItemSlotV2 {
    @EmbeddedId // ID 객체를 통째로 박아 넣음
    private SlotEmbeddedId id;

    private String itemName;
}
