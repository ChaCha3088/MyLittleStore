package site.mylittlestore.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import site.mylittlestore.domain.Address;
import site.mylittlestore.dto.member.MemberUpdateDto;
import site.mylittlestore.enumstorage.status.StoreStatus;
import site.mylittlestore.dto.member.MemberCreationDto;
import site.mylittlestore.dto.member.MemberFindDto;
import site.mylittlestore.dto.member.MemberPasswordUpdateDto;
import site.mylittlestore.dto.store.StoreDto;
import site.mylittlestore.dto.store.StoreUpdateDto;
import site.mylittlestore.enumstorage.errormessage.StoreErrorMessage;
import site.mylittlestore.exception.member.DuplicateMemberException;
import site.mylittlestore.exception.member.IsNotMembersStoreException;
import site.mylittlestore.exception.member.NoSuchMemberException;
import site.mylittlestore.exception.store.DuplicateStoreNameException;
import site.mylittlestore.repository.member.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class MemberServiceTest {
    @Autowired
    private MemberService memberService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager em;

    private Long memberTestId;

    private Long storeTestId;

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

        Long newStoreId = memberService.createStore(StoreDto.builder()
                .name("storeTest")
                .memberId(newMemberId)
                .address(Address.builder()
                        .city("city")
                        .street("street")
                        .zipcode("zipcode")
                        .build())
                .build());

        memberTestId = newMemberId;
        storeTestId = newStoreId;
    }

    /**
     * Id로 회원님 찾기
     */
    @Test
    void findMemberById() {
        //when
        MemberFindDto memberFindDtoById = memberService.findMemberFindDtoByMemberId(memberTestId);

        //then
        assertThat(memberFindDtoById.getName()).isEqualTo("memberTest");
    }

    @Test
    void findMemberByIdException() {
        //then
        assertThatThrownBy(() -> {
            memberService.findMemberFindDtoByMemberId(1234L);
        }).isInstanceOf(NoSuchMemberException.class);
    }

    @Test
    void findMemberByMemberEmail() {
        //when
        MemberFindDto memberFindDtoByEmail = memberService.findMemberByMemberEmail("memberTest@gmail.com");

        //then
        assertThat(memberFindDtoByEmail.getName()).isEqualTo("memberTest");
    }

    @Test
    void findMemberByMemberEmailException() {
        //then
        assertThatThrownBy(() -> {
            memberService.findMemberByMemberEmail("asdfg@gmail.com");
        }).isInstanceOf(NoSuchMemberException.class)
        .hasMessageContaining("해당하는 email을 가진 회원님이 없습니다.");
    }

    @Test
    public void findAllMember(){
        //given
        memberService.joinMember(MemberCreationDto.builder()
                .name("memberTestA")
                .email("memberTestA@gmail.com")
                .password("password")
                .address(Address.builder()
                        .city("city")
                        .street("street")
                        .zipcode("zipcode")
                        .build())
                .build());

        memberService.joinMember(MemberCreationDto.builder()
                .name("memberTestB")
                .email("memberTestB@gmail.com")
                .password("password")
                .address(Address.builder()
                        .city("city")
                        .street("street")
                        .zipcode("zipcode")
                        .build())
                .build());

        //when
        List<MemberFindDto> findAllMember = memberService.findAllMemberFindDto();

        //then
        assertThat(findAllMember.size()).isEqualTo(3);
    }

    /**
     * 회원님 가입
     */
    @Test
    void joinMember() {
        //given
        MemberCreationDto memberCreationDto = MemberCreationDto.builder()
                .name("memberTestB")
                .email("memberTestB@gmail.com")
                .password("password")
                .address(Address.builder()
                        .city("city")
                        .street("street")
                        .zipcode("zipcode")
                        .build())
                .build();

        //when
        //회원님 가입 service 메소드 호출
        Long createdMemberId = memberService.joinMember(memberCreationDto);

        //영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        //then
        //회원님 가입 잘 됐는지 db 확인
        MemberFindDto findMemberFindDtoById = memberService.findMemberFindDtoByMemberId(createdMemberId);

        assertThat(findMemberFindDtoById.getName()).isEqualTo("memberTestB");
    }

    /**
     * 회원님 가입 시 이미 있는 이메일이면 예외 발생
     */
    @Test
    void joinMemberException() {
        //given
        MemberCreationDto memberCreationDto = MemberCreationDto.builder()
                .name("memberTest")
                .email("memberTest@gmail.com")
                .password("password")
                .address(Address.builder()
                        .city("city")
                        .street("street")
                        .zipcode("zipcode")
                        .build())
                .build();

        //then
        //회원님 가입 잘 됐는지 db 확인
        assertThatThrownBy(() -> {
            memberService.joinMember(memberCreationDto);
        }).isInstanceOf(DuplicateMemberException.class);
    }

    @Test
    void updateMember() {
        //given
        memberService.updateMember(MemberUpdateDto.builder()
                .id(memberTestId)
                .name("Cha Cha")
                .address(Address.builder()
                        .city("newCity")
                        .street("newStreet")
                        .zipcode("newZipcode")
                        .build())
                .build());

        //영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        //when
        MemberFindDto findMemberFindDtoById = memberService.findMemberFindDtoByMemberId(memberTestId);

        //then
        assertThat(findMemberFindDtoById.getName()).isEqualTo("Cha Cha");
        assertThat(findMemberFindDtoById.getAddress().getCity()).isEqualTo("newCity");
        assertThat(findMemberFindDtoById.getAddress().getStreet()).isEqualTo("newStreet");
        assertThat(findMemberFindDtoById.getAddress().getZipcode()).isEqualTo("newZipcode");
    }

    /**
     * 회원님 비밀번호 수정
     */
    @Test
    void updateMemberPassword() {
        //when
        memberService.updateMemberPassword(MemberPasswordUpdateDto.builder()
                .id(memberTestId)
                .password("password")
                .newPassword("Cha Cha")
                .build());

        //영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        //then
        assertThat(memberRepository.findById(memberTestId).get().getPassword())
                .isEqualTo("Cha Cha");
    }

    /**
     * 회원님 가게 생성
     */
    @Test
    void createStore() {
        //when
        Long createdStoreId = memberService.createStore(StoreDto.builder()
                .name("storeTestB")
                .memberId(memberTestId)
                .address(Address.builder()
                        .city("city")
                        .street("street")
                        .zipcode("zipcode")
                        .build())
                .build());

        //영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        //then
        //회원님 가게 생성 잘 됐는지 db 확인
        StoreDto findStoreDtoById = storeService.findStoreDtoById(createdStoreId);

        assertThat(findStoreDtoById.getName()).isEqualTo("storeTestB");
    }

    /**
     * 회원님 가게 생성
     * 이름이 같은 가게가 이미 있으면, 예외 발생
     */
    @Test
    void createStoreDuplicateStoreException() {
        //이름이 같은 가게를 생성하면, 예외 발생
        Assertions.assertThatThrownBy(() -> {
            memberService.createStore(StoreDto.builder()
                    .name("storeTest")
                    .memberId(memberTestId)
                    .address(Address.builder()
                            .city("city")
                            .street("street")
                            .zipcode("zipcode")
                            .build())
                    .build());
        }).isInstanceOf(DuplicateStoreNameException.class);
    }

    @Test
    public void updateStoreNameAndAddress(){
        //when
        Long updateStoreId = memberService.updateStore(StoreUpdateDto.builder()
                .id(storeTestId)
                .memberId(memberTestId)
                .newName("newStoreTest")
                .newAddress(Address.builder()
                        .city("newCity")
                        .street("newStreet")
                        .zipcode("newZipcode")
                        .build())
                .build());

        //영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        StoreDto updatedStoreDtoById = storeService.findStoreDtoById(updateStoreId);

        //then
        assertThat(updatedStoreDtoById.getName()).isEqualTo("newStoreTest");
        assertThat(updatedStoreDtoById.getAddress().getCity()).isEqualTo("newCity");
    }

    @Test
    public void updateStoreNameAndAddressDuplicateStoreNameException(){
        //given
        Long createdStoreId = memberService.createStore(StoreDto.builder()
                .name("storeTestB")
                .memberId(memberTestId)
                .address(Address.builder()
                        .city("city")
                        .street("street")
                        .zipcode("zipcode")
                        .build())
                .build());

        //영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        //then
        assertThatThrownBy(() -> {
            memberService.updateStore(StoreUpdateDto.builder()
                    .id(createdStoreId)
                    .memberId(memberTestId)
                    .newName("storeTestB")
                    .newAddress(Address.builder()
                            .city("newCity")
                            .street("newStreet")
                            .zipcode("newZipcode")
                            .build())
                    .build());
        }).isInstanceOf(DuplicateStoreNameException.class);
    }

    /**
     * 가게 이름만 수정
     */
    @Test
    public void updateStoreOnlyName(){
        //when
        Long updateStoreId = memberService.updateStore(StoreUpdateDto.builder()
                .id(storeTestId)
                .memberId(memberTestId)
                .newName("newStoreTest")
                .build());

        //영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        StoreDto updatedStoreDtoById = storeService.findStoreDtoById(updateStoreId);

        //then
        assertThat(updatedStoreDtoById.getName()).isEqualTo("newStoreTest");
    }

    /**
     * 가게 주소만 수정
     */
    @Test
    public void updateStoreOnlyAddress(){
        //when
        Long updateStoreId = memberService.updateStore(StoreUpdateDto.builder()
                .id(storeTestId)
                .memberId(memberTestId)
                .newAddress(Address.builder()
                        .city("newCity")
                        .street("newStreet")
                        .zipcode("newZipcode")
                        .build())
                .build());

        //영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        StoreDto updatedStoreDtoById = storeService.findStoreDtoById(updateStoreId);

        //then
        assertThat(updatedStoreDtoById.getAddress().getCity()).isEqualTo("newCity");
    }

    @Test
    @DisplayName("가게 상태 변경(CLOSE -> OPEN)")
    public void changeStoreStatusCloseToOpen() {
        //given
        memberService.changeStoreStatus(StoreUpdateDto.builder()
                .id(storeTestId)
                .memberId(memberTestId)
                .build());

        //영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        //when
        StoreDto findStoreById = storeService.findStoreDtoById(storeTestId);

        //then
        assertThat(findStoreById.getStoreStatus()).isEqualTo(StoreStatus.OPEN);
    }

    @Test
    @DisplayName("가게 상태 변경(OPEN -> CLOSE)")
    public void changeStoreStatusOpenToClose() {
        //given
        memberService.changeStoreStatus(StoreUpdateDto.builder()
                .id(storeTestId)
                .memberId(memberTestId)
                .build());

        //영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        //when
        StoreDto findStoreById1 = storeService.findStoreDtoById(storeTestId);

        //then
        assertThat(findStoreById1.getStoreStatus()).isEqualTo(StoreStatus.OPEN);

        //given
        memberService.changeStoreStatus(StoreUpdateDto.builder()
                .id(storeTestId)
                .memberId(memberTestId)
                .build());

        //영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        //when
        StoreDto findStoreById2 = storeService.findStoreDtoById(storeTestId);

        //then
        assertThat(findStoreById2.getStoreStatus()).isEqualTo(StoreStatus.CLOSE);
    }

    @Test
    public void changeStoreStatusIsNotMembersStoreException() {
        //given
        Long newMemberId = memberService.joinMember(MemberCreationDto.builder()
                .name("Cha Cha")
                .email("cha3088@gmail.com")
                .password("password")
                .address(Address.builder()
                        .city("city")
                        .street("street")
                        .zipcode("zipcode")
                        .build())
                .build());

        //then
        assertThatThrownBy(() -> memberService.changeStoreStatus(StoreUpdateDto.builder()
                .id(storeTestId)
                .memberId(newMemberId)
                .build())).isInstanceOf(IsNotMembersStoreException.class)
                .hasMessageContaining(StoreErrorMessage.IS_NOT_MEMBERS_STORE.getMessage());
    }
}