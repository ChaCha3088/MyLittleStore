package site.mylittlestore.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import site.mylittlestore.domain.Address;
import site.mylittlestore.domain.Member;
import site.mylittlestore.dto.member.MemberCreationDto;
import site.mylittlestore.dto.member.MemberFindDto;
import site.mylittlestore.repository.member.MemberRepository;
import site.mylittlestore.service.MemberService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    private MemberService memberService;

    @PersistenceContext
    private EntityManager em;

    private Long memberTestId;
    private String memberTestEmail;

    @BeforeAll
    void setUp() {
        Long newMemberId = memberService.joinMember(MemberCreationDto.builder()
                .name("memberTest")
                .email("memberTest@gmail.com")
                .password("password")
                .address(Address.builder()
                        .city("city")
                        .street("street")
                        .zipcode("zipcode")
                        .build())
                .build());

        memberTestId = newMemberId;
        memberTestEmail = "memberTest@gmail.com";
    }

    @Test
    void findMemberFindDtoById() {
        //조회
        Optional<MemberFindDto> findMemberFindDtoById = memberRepository.findMemberFindDtoById(memberTestId);

        //검증
        assertThat(findMemberFindDtoById.get().getId()).isEqualTo(memberTestId);
        assertThat(findMemberFindDtoById.get().getName()).isEqualTo("memberTest");
    }

    @Test
    void findMemberByName() {
        //조회
        Optional<Member> findMember = memberRepository.findMemberByName("memberTest");

        //검증
        assertThat(findMember.get().getId()).isEqualTo(memberTestId);

        //영속성 컨텍스트 비우기
        em.flush();
        em.clear();

        //조회
        Optional<MemberFindDto> findMemberFindDtoByEmail = memberRepository.findMemberFindDtoByEmail("memberTest@gmail.com");

        //검증
        assertThat(findMemberFindDtoByEmail.get().getId()).isEqualTo(memberTestId);
    }

    @Test
    void findMemberByEmail() {
        //조회
        Optional<MemberFindDto> findMemberFindDto = memberRepository.findMemberFindDtoByEmail(memberTestEmail);

        //검증
        assertThat(findMemberFindDto.get().getId()).isEqualTo(memberTestId);
    }
}