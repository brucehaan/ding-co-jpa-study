package ding.co.hellojpa.week3;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberTeamDto {
    private String memberName;
    private String teamName;
}
