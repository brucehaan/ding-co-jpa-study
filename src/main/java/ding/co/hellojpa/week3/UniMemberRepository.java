package ding.co.hellojpa.week3;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UniMemberRepository extends JpaRepository<UniMember, Long> {
    // 1. fetch join : 한방 쿼리로 진짜 객체를 다 가져오는 메서드
    @Query("select m from UniMember m join fetch m.team where m.id = :id")
    Optional<UniMember> findByIdFetchJoin(@Param("id") Long id);

    // 2. dto 조회 : 엔티티가 아니라 dto로 바로 꽂아주는 메서드
    @Query("select new ding.co.hellojpa.week3.MemberTeamDto(m.username, t.name) " +
            "from UniMember m join m.team t where m.id = :id")
    Optional<MemberTeamDto> findDtoById(@Param("id") Long id);

}
