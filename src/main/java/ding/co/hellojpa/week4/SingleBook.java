package ding.co.hellojpa.week4;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("B") // DTYPE 컬럼에 'B'로 저장됨
@Getter
@Setter
public class SingleBook extends SingleItem {
    private String author;
    private String isbn;
}
