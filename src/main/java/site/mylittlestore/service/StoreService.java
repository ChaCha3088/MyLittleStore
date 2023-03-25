package site.mylittlestore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.mylittlestore.domain.Store;
import site.mylittlestore.domain.StoreTable;
import site.mylittlestore.domain.item.Item;
import site.mylittlestore.dto.item.ItemCreationDto;
import site.mylittlestore.dto.item.ItemUpdateDto;
import site.mylittlestore.dto.store.StoreDto;
import site.mylittlestore.dto.store.StoreOnlyDto;
import site.mylittlestore.dto.store.StoreTableCreationDto;
import site.mylittlestore.enumstorage.errormessage.ItemErrorMessage;
import site.mylittlestore.enumstorage.errormessage.StoreErrorMessage;
import site.mylittlestore.exception.item.DuplicateItemException;
import site.mylittlestore.exception.item.NoSuchItemException;
import site.mylittlestore.exception.store.NoSuchStoreException;
import site.mylittlestore.repository.item.ItemRepository;
import site.mylittlestore.repository.store.StoreRepository;
import site.mylittlestore.repository.storetable.StoreTableRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final ItemRepository itemRepository;

    private final StoreTableRepository storeTableRepository;

    public StoreDto findStoreDtoById(Long id) throws NoSuchStoreException {
        Optional<Store> findStore = storeRepository.findById(id);

        //가게가 없으면 예외 발생
        return findStore.orElseThrow(() -> new NoSuchStoreException(StoreErrorMessage.NO_SUCH_STORE.getMessage())).toStoreDto();
    }

    public StoreOnlyDto findStoreOnlyDtoById(Long id) throws NoSuchStoreException {
        Optional<Store> findStore = storeRepository.findById(id);

        //가게가 없으면 예외 발생
        return findStore.orElseThrow(() -> new NoSuchStoreException(StoreErrorMessage.NO_SUCH_STORE.getMessage())).toStoreOnlyDto();
    }

    public List<StoreDto> findAllStoreDtoByMemberId(Long memberId) {
        //회원님 id를 가지고 있는 가게를 찾아야지.
        List<Store> findStoreByStoreId = storeRepository.findAllStoreByMemberId(memberId);

        //Dto로 변환
        return findStoreByStoreId.stream()
                .map(m -> m.toStoreDto())
                .collect(Collectors.toList());
    }

    @Transactional
    public Long createItem(ItemCreationDto itemCreationDto) throws NoSuchStoreException {
        Store findStoreById = findById(itemCreationDto.getStoreId());

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

    private void validateDuplicateItemWithItemName(String name) throws DuplicateItemException {
        Optional<Item> findItemByItemName = itemRepository.findItemByName(name);

        //같은 이름의 상품이 있으면, 예외 발생
        findItemByItemName.ifPresent(m -> {
            throw new DuplicateItemException("이미 존재하는 상품입니다.");
        });
    }

    private Store findById(Long id) throws NoSuchStoreException {
        return storeRepository.findById(id).orElseThrow(() -> new NoSuchStoreException(StoreErrorMessage.NO_SUCH_STORE.getMessage()));
    }
}
