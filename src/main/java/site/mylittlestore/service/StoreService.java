package site.mylittlestore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.mylittlestore.domain.Member;
import site.mylittlestore.domain.Store;
import site.mylittlestore.domain.item.Item;
import site.mylittlestore.dto.item.ItemCreationDto;
import site.mylittlestore.dto.item.ItemUpdateDto;
import site.mylittlestore.dto.store.StoreCreationDto;
import site.mylittlestore.dto.store.StoreDtoWithStoreTablesAndItems;
import site.mylittlestore.dto.store.StoreDto;
import site.mylittlestore.dto.store.StoreUpdateDto;
import site.mylittlestore.enumstorage.errormessage.ItemErrorMessage;
import site.mylittlestore.enumstorage.errormessage.MemberErrorMessage;
import site.mylittlestore.enumstorage.errormessage.StoreErrorMessage;
import site.mylittlestore.enumstorage.status.StoreStatus;
import site.mylittlestore.exception.item.DuplicateItemException;
import site.mylittlestore.exception.item.NoSuchItemException;
import site.mylittlestore.exception.member.NoSuchMemberException;
import site.mylittlestore.exception.store.NoSuchStoreException;
import site.mylittlestore.repository.item.ItemRepository;
import site.mylittlestore.repository.member.MemberRepository;
import site.mylittlestore.repository.store.StoreRepository;
import site.mylittlestore.repository.storetable.StoreTableRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreService {
    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final StoreTableRepository storeTableRepository;
    private final ItemRepository itemRepository;

    public StoreDto findStoreDtoById(Long id) throws NoSuchStoreException {
        return storeRepository.findById(id)
                //가게가 없으면 예외 발생
                .orElseThrow(() -> new NoSuchStoreException(StoreErrorMessage.NO_SUCH_STORE.getMessage()))
                //Dto로 변환
                .toStoreDto();
    }

    public StoreDtoWithStoreTablesAndItems findStoreDtoWithStoreTablesAndItemsById(Long id) throws NoSuchStoreException {
        return storeRepository.findById(id)
                //가게가 없으면 예외 발생
                .orElseThrow(() -> new NoSuchStoreException(StoreErrorMessage.NO_SUCH_STORE.getMessage()))
                //Dto로 변환
                .toStoreDtoWithStoreTablesAndItems();
    }

    public List<StoreDto> findAllStoreDtoById(Long memberId) {
        //회원 id를 가지고 있는 가게를 찾아야지.
        return storeRepository.findAllStoreByMemberId(memberId).stream()
                //Dto로 변환
                .map(m -> m.toStoreDto())
                .collect(Collectors.toList());
    }

    @Transactional
    public Long createStore(StoreCreationDto storeCreationDto) throws NoSuchMemberException {
        //가게 생성
        Member member = findMemberById(storeCreationDto.getMemberId());

        Store createdStore = Store.builder()
                .member(member)
                .name(storeCreationDto.getName())
                .city(storeCreationDto.getCity())
                .street(storeCreationDto.getStreet())
                .zipcode(storeCreationDto.getZipcode())
                .build();

        //가게 저장
        Store savedStore = storeRepository.save(createdStore);

        //회원의 정보 저장
        memberRepository.save(member);

        return savedStore.getId();
    }

    /**
     * 가게 이름과 주소를 수정
     * 둘 중에 하나만 수정해도 됨
     * @param storeUpdateDto
     * @return
     */
    @Transactional
    public Long updateStore(StoreUpdateDto storeUpdateDto) {
        //업데이트 하려는 가게가 회원의 가게인지 검증
        Store findStore = validateStoreIsMembersStore(storeUpdateDto);

        //가게 정보 업데이트
        findStore.updateStoreName(storeUpdateDto.getName());
        findStore.updateStoreAddress(storeUpdateDto.getCity(), storeUpdateDto.getStreet(), storeUpdateDto.getZipcode());

        //저장
        Store savedStore = storeRepository.save(findStore);

        return savedStore.getId();
    }

    /**
     * 가게 열기 / 닫기 토글
     */
    @Transactional
    public Long changeStoreStatus(StoreUpdateDto storeUpdateDto) {
        //업데이트 하려는 가게가 회원의 가게인지 검증
        Store findStore = validateStoreIsMembersStore(storeUpdateDto);

        //가게 상태 변경
        if (findStore.getStoreStatus() == StoreStatus.OPEN) {
            findStore.changeStoreStatus(StoreStatus.CLOSE);
        } else if (findStore.getStoreStatus() == StoreStatus.CLOSE) {
            findStore.changeStoreStatus(StoreStatus.OPEN);
        }

        //저장
        return storeRepository.save(findStore).getId();
    }

    @Transactional
    public Long createItem(ItemCreationDto itemCreationDto) throws NoSuchStoreException {
        Store findStoreById = findStoreById(itemCreationDto.getStoreId());

        //상품 생성
        Item createdItem = Item.builder()
                .store(findStoreById)
                .name(itemCreationDto.getName())
                .price(itemCreationDto.getPrice())
                .stock(itemCreationDto.getStock())
                .build();

        Store updatedStore = findStoreById.createItem(createdItem);

        //상품 저장
        Item savedItem = itemRepository.save(createdItem);

        //가게 저장
        storeRepository.save(updatedStore);

        return savedItem.getId();
    }

    @Transactional
    public Long updateItem(ItemUpdateDto itemUpdateDto) throws NoSuchStoreException, NoSuchItemException {
        //업데이트 하려는 상품이 가게에 있는지 검증
        Item findItemByIdAndStoreId = itemRepository.findItemByIdAndStoreId(itemUpdateDto.getId(), itemUpdateDto.getStoreId())
                .orElseThrow(() -> new NoSuchItemException(ItemErrorMessage.NO_SUCH_ITEM.getMessage()));

        //상품 정보 업데이트
        findItemByIdAndStoreId.updateName(itemUpdateDto.getNewItemName());
        findItemByIdAndStoreId.updatePrice(itemUpdateDto.getNewPrice());
        findItemByIdAndStoreId.updateStock(itemUpdateDto.getNewStock());

        //저장
        Item savedItem = itemRepository.save(findItemByIdAndStoreId);

        return savedItem.getId();
    }

    private Member findMemberById(Long memberId) throws NoSuchMemberException {
        return memberRepository.findById(memberId)
                //해당하는 Id를 가진 회원이 없으면, 예외 발생
                .orElseThrow(() -> new NoSuchMemberException(MemberErrorMessage.NO_SUCH_MEMBER.getMessage()));
    }

    private void validateDuplicateItemWithItemName(String name) throws DuplicateItemException {
        Optional<Item> findItemByItemName = itemRepository.findItemByName(name);

        //같은 이름의 상품이 있으면, 예외 발생
        findItemByItemName.ifPresent(m -> {
            throw new DuplicateItemException("이미 존재하는 상품입니다.");
        });
    }

    private Store findStoreById(Long id) throws NoSuchStoreException {
        return storeRepository.findById(id).orElseThrow(() -> new NoSuchStoreException(StoreErrorMessage.NO_SUCH_STORE.getMessage()));
    }

    //업데이트 하려는 가게가 회원의 가게인지 검증
    private Store validateStoreIsMembersStore(StoreUpdateDto storeUpdateDto) {
        return storeRepository.findStoreByIdAndMemberId(storeUpdateDto.getId(), storeUpdateDto.getMemberId())
                //storeId와 memberId에 맞는 가게가 없으면, 예외 발생
                .orElseThrow(() -> new NoSuchStoreException(StoreErrorMessage.IS_NOT_MEMBERS_STORE.getMessage()));
    }
}
