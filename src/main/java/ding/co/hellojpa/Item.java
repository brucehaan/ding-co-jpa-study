package ding.co.hellojpa;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id // @GeneratedValue 없음! (우리가 직접 ID를 넣어서 테스트할 것임)
    private Long id;

    private String name;
}