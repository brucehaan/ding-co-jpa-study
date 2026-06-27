package ding.co.hellojpa.week4;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter @Getter
public class MappedMember extends BaseEntity{
    @Id @GeneratedValue
    private Long id;
    private String name;
}
