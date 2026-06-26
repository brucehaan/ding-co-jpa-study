package ding.co.hellojpa.week4;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@DiscriminatorValue("M") // DTYPE 컬럼에 저장될 값 지정(기본값은 엔티티 이름인 'JoinedMovie')
public class JoinedMovie extends JoinedItem {
    private String director;
    private String actor;
}
