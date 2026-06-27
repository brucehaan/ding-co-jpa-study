package ding.co.hellojpa.week4;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("M") // DTYPE 컬럼에 'M'으로 저장됨
@Getter
@Setter
public class SingleMovie extends SingleItem {
    private String director;
    private String actor;
}
