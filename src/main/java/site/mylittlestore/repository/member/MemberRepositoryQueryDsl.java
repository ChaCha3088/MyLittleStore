package site.mylittlestore.repository.member;

import org.springframework.data.repository.query.Param;
import site.mylittlestore.domain.Member;
import site.mylittlestore.dto.member.MemberFindDto;

import java.util.List;
import java.util.Optional;

public interface MemberRepositoryQueryDsl {

    Optional<MemberFindDto> findMemberFindDtoById(Long id);

    Optional<MemberFindDto> findMemberFindDtoByEmail(@Param("email") String email);

    List<MemberFindDto> findAllMemberFindDto();

    Optional<Member> findMemberByMemberIdAndStoreName(Long memberId, String storeName);

}
