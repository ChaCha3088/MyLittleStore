package site.mylittlestore.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import site.mylittlestore.domain.Member;
import site.mylittlestore.dto.member.MemberFindDto;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryQueryDsl {

    Optional<MemberFindDto> findMemberFindDtoById(Long id);

    Optional<Member> findMemberByName(@Param("name") String name);

    Optional<MemberFindDto> findMemberFindDtoByEmail(@Param("email") String email);

    List<MemberFindDto> findAllMemberFindDto();
}
