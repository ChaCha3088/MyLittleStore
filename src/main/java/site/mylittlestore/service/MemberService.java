package site.mylittlestore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.mylittlestore.domain.Address;
import site.mylittlestore.domain.Member;
import site.mylittlestore.domain.Store;
import site.mylittlestore.dto.member.MemberCreationDto;
import site.mylittlestore.dto.member.MemberFindDto;
import site.mylittlestore.dto.member.MemberPasswordUpdateDto;
import site.mylittlestore.dto.member.MemberUpdateDto;
import site.mylittlestore.dto.store.StoreDtoWithStoreTableFindDtosAndItemFindDtos;
import site.mylittlestore.dto.store.StoreUpdateDto;
import site.mylittlestore.enumstorage.errormessage.MemberErrorMessage;
import site.mylittlestore.enumstorage.errormessage.StoreErrorMessage;
import site.mylittlestore.enumstorage.status.StoreStatus;
import site.mylittlestore.exception.member.DuplicateMemberException;
import site.mylittlestore.exception.member.IsNotMembersStoreException;
import site.mylittlestore.exception.member.MemberPasswordDoesNotMatchException;
import site.mylittlestore.exception.member.NoSuchMemberException;
import site.mylittlestore.exception.store.DuplicateStoreNameException;
import site.mylittlestore.exception.store.MemberHaveDuplicateStoreException;
import site.mylittlestore.repository.member.MemberRepository;
import site.mylittlestore.repository.store.StoreRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {


    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;


    public MemberFindDto findMemberFindDtoByMemberId(Long memberId) throws NoSuchMemberException {
        Optional<MemberFindDto> findMemberById = memberRepository.findMemberFindDtoById(memberId);

        //회원이 없으면 예외 발생
        return findMemberById.orElseThrow(() -> new NoSuchMemberException(MemberErrorMessage.NO_SUCH_MEMBER.getMessage()));
    }


    public MemberFindDto findMemberByMemberEmail(String memberEmail) throws NoSuchMemberException {
        Optional<MemberFindDto> findMemberFindDtoByMemberEmail = findMemberFindDtoByEmail(memberEmail);

        //해당하는 이메일을 가진 회원이 없으면, 예외 발생
        return findMemberFindDtoByMemberEmail.orElseThrow(() -> new NoSuchMemberException(MemberErrorMessage.NO_SUCH_MEMBER_WITH_THAT_EMAIL.getMessage()));
    }

    public List<MemberFindDto> findAllMemberFindDto() {
        List<MemberFindDto> findAllMemberFindDto = memberRepository.findAllMemberFindDto();

        return findAllMemberFindDto;
    }

    @Transactional
    public Long joinMember(MemberCreationDto memberCreationDto) throws DuplicateMemberException {
        //같은 이메일을 가진 회원이 있는지 검증
        validateDuplicateMember(memberCreationDto.getEmail());

        Member saveMember = memberRepository.save(Member.builder()
                        .name(memberCreationDto.getName())
                        .email(memberCreationDto.getEmail())
                        .password(memberCreationDto.getPassword())
                        .address(memberCreationDto.getAddress())
                .build());

        return saveMember.getId();
    }

    @Transactional
    public Long updateMember(MemberUpdateDto memberUpdateDto) throws NoSuchMemberException {
        Optional<String> newMemberName = Optional.ofNullable(memberUpdateDto.getName());
        Optional<Address> newMemberAddress = Optional.ofNullable(memberUpdateDto.getAddress());

        //업데이트하려는 회원이 있는지 검증
        Member findMemberById = findById(memberUpdateDto.getId());

        //회원의 정보 업데이트
        newMemberName.ifPresent(newName -> findMemberById.updateMemberName(newName));
        newMemberAddress.ifPresent(newAddress -> findMemberById.updateMemberAddress(newAddress));

        //저장
        Member savedMember = memberRepository.save(findMemberById);

        return savedMember.getId();
    }

    @Transactional
    public void updateMemberPassword(MemberPasswordUpdateDto memberPasswordUpdateDto) throws NoSuchMemberException, MemberPasswordDoesNotMatchException {
        Member findMemberById = findById(memberPasswordUpdateDto.getId());

        //비밀번호 검증 먼저
        if (!findMemberById.getPassword().equals(memberPasswordUpdateDto.getPassword())) {
            throw new MemberPasswordDoesNotMatchException(MemberErrorMessage.PASSWORD_DOES_NOT_MATCH.getMessage());
        }

        //회원의 정보 업데이트
        findMemberById.updateMemberPassword(memberPasswordUpdateDto.getNewPassword());

        //회원의 정보 저장
        memberRepository.save(findMemberById);
    }

    @Transactional
    public Long createStore(StoreDtoWithStoreTableFindDtosAndItemFindDtos storeDtoWithStoreTableFindDtosAndItemFindDtos) throws DuplicateStoreNameException, NoSuchMemberException {
        //이미 있는 가게인지 확인
        validateDuplicateStoreWithStoreName(storeDtoWithStoreTableFindDtosAndItemFindDtos.getName());

//        //회원이 이미 가지고 있는 가게인지 검증
//        Member findMemberById = validateMemberAlreadyHaveThatStoreWithSameStoreName(storeDto.getMemberId(), storeDto.getName());

        //가게 생성
        Member findMemberById = findById(storeDtoWithStoreTableFindDtosAndItemFindDtos.getMemberId());

        Store createdStore = Store.builder()
                .member(findMemberById)
                .name(storeDtoWithStoreTableFindDtosAndItemFindDtos.getName())
                .address(Address.builder()
                        .city(storeDtoWithStoreTableFindDtosAndItemFindDtos.getAddressDto().getCity())
                        .street(storeDtoWithStoreTableFindDtosAndItemFindDtos.getAddressDto().getStreet())
                        .zipcode(storeDtoWithStoreTableFindDtosAndItemFindDtos.getAddressDto().getZipcode())
                        .build())
                .build();

        Member updatedMember = findMemberById.createStore(createdStore);

        //가게 저장
        Store savedStore = storeRepository.save(createdStore);

        //회원의 정보 저장
        Member savedMember = memberRepository.save(updatedMember);

        return savedStore.getId();
    }

    /**
     * 가게 이름과 주소를 수정
     * 둘 중에 하나만 수정해도 됨
     * @param storeUpdateDto
     * @return
     * @throws IsNotMembersStoreException
     */
    @Transactional
    public Long updateStore(StoreUpdateDto storeUpdateDto) throws DuplicateStoreNameException, NoSuchMemberException, IsNotMembersStoreException {
        Optional<String> newName = Optional.ofNullable(storeUpdateDto.getNewName());
        Optional<Address> newAddress = Optional.ofNullable(storeUpdateDto.getNewAddress());

        //새로운 가게 이름과 같은 이름의 가게가 있는지 검증
        newName.ifPresent(m -> validateDuplicateStoreWithStoreName(storeUpdateDto.getNewName()));

        //업데이트 하려는 가게가 회원의 가게인지 검증
        Store findStore = validateStoreIsMembersStore(storeUpdateDto);

        //가게 정보 업데이트
        newName.ifPresent(m -> findStore.updateStoreName(m));
        newAddress.ifPresent(m -> findStore.updateStoreAddress(m));

        //저장
        Store savedStore = storeRepository.save(findStore);

        return savedStore.getId();
    }

    /**
     * 가게 열기 / 닫기 토글
     */
    @Transactional
    public Long changeStoreStatus(StoreUpdateDto storeUpdateDto) throws NoSuchMemberException, IsNotMembersStoreException {
        //업데이트 하려는 가게가 회원의 가게인지 검증
        Store findStore = validateStoreIsMembersStore(storeUpdateDto);

        //가게 상태 변경
        if (findStore.getStoreStatus() == StoreStatus.OPEN) {
            findStore.changeStoreStatus(StoreStatus.CLOSE);
        } else if (findStore.getStoreStatus().equals(StoreStatus.CLOSE.toString())) {
            findStore.changeStoreStatus(StoreStatus.OPEN);
        }

        //저장
        return storeRepository.save(findStore).getId();
    }

    private Member findById(Long memberId) throws NoSuchMemberException {
        Optional<Member> findMemberById = memberRepository.findById(memberId);

        //해당하는 Id를 가진 회원이 없으면, 예외 발생
        if (findMemberById.isEmpty()) {
            throw new NoSuchMemberException("해당하는 Id를 가진 회원이 없습니다.");
        }

        return findMemberById.get();
    }

    private Optional<MemberFindDto> findMemberFindDtoByEmail(String memberEmail) throws NoSuchMemberException {
        Optional<MemberFindDto> findMemberFindDtoByMemberEmail = memberRepository.findMemberFindDtoByEmail(memberEmail);

        findMemberFindDtoByMemberEmail.orElseThrow(() -> new NoSuchMemberException("해당하는 이메일을 가진 회원이 없습니다."));

        return findMemberFindDtoByMemberEmail;
    }

    private void validateDuplicateMember(String memberEmail) throws DuplicateMemberException {
        Optional<MemberFindDto> findMemberFindDtoByEmail = memberRepository.findMemberFindDtoByEmail(memberEmail);

        //같은 이메일을 가진 회원이 있으면, 예외 발생
        findMemberFindDtoByEmail.ifPresent(m -> {
            throw new DuplicateMemberException("이미 존재하는 회원입니다.");
        });
    }

    /**
     * 회원이 이미 같은 이름의 가게를 가지고 있는지 검증
     * @param targetMemberId
     * @param newStoreName
     * @return
     * @throws IllegalStateException
     */
    private Member validateMemberAlreadyHaveThatStoreWithSameStoreName(Long targetMemberId, String newStoreName) throws NoSuchMemberException, MemberHaveDuplicateStoreException {
        Member findMemberById = findById(targetMemberId);

        //회원이 같은 이름의 가게를 가지고 있으면, 예외 발생
        if (findMemberById.getStores().contains(newStoreName)) {
            throw new MemberHaveDuplicateStoreException("회원이 이미 같은 이름의 가게를 가지고 있습니다.");
        }

        return findMemberById;
    }

    /**
     * 데이터 베이스에 같은 이름의 가게가 있는지 검증
     * @param newStoreName
     * @throws IllegalStateException
     */
    private void validateDuplicateStoreWithStoreName(String newStoreName) throws DuplicateStoreNameException {
        Optional<Store> storeFindByName = storeRepository.findStoreByName(newStoreName);

        //같은 이름의 가게가 있으면, 예외 발생
        storeFindByName.ifPresent(m -> {
            throw new DuplicateStoreNameException(StoreErrorMessage.DUPLICATE_STORE_NAME.getMessage());
        });
    }

    //업데이트 하려는 가게가 회원의 가게인지 검증
    private Store validateStoreIsMembersStore(StoreUpdateDto storeUpdateDto) throws IsNotMembersStoreException {
        Optional<Store> findStoreByIdAndMemberId = storeRepository.findStoreByIdAndMemberId(storeUpdateDto.getId(), storeUpdateDto.getMemberId());

        //비어있으면 오류 발생
        return findStoreByIdAndMemberId
                    .orElseThrow(() -> new IsNotMembersStoreException(StoreErrorMessage.IS_NOT_MEMBERS_STORE.getMessage()));
    }
}
