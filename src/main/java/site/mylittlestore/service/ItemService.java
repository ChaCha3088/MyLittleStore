package site.mylittlestore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.mylittlestore.dto.item.ItemFindDto;
import site.mylittlestore.enumstorage.errormessage.ItemErrorMessage;
import site.mylittlestore.exception.item.NoSuchItemException;
import site.mylittlestore.repository.item.ItemRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemFindDto findItemDtoById(Long id) throws NoSuchItemException {
        Optional<ItemFindDto> findItemDtoById = itemRepository.findItemDtoById(id);

        //아이템이 없으면 예외 발생
        return findItemDtoById.orElseThrow(() -> new NoSuchItemException(ItemErrorMessage.NO_SUCH_ITEM.getMessage()));
    }

    public List<ItemFindDto> findAllItemDtoByStoreId(Long storeId) {
        //가게에 속한 아이템만 찾아야지.
        return itemRepository.findAllItemDtoByStoreId(storeId);
    }

    //StoreService에서 이미 만듦

//    @Transactional
//    public Long createItem(ItemDto itemDto) throws IllegalStateException {
//        validateDuplicateItem(item.getName());
//
//        Item saveItem = itemRepository.save(item);
//        return saveItem.getId();
//    }

//    @Transactional
//    public void updateItem(Long id, String name, int price, int stock) throws IllegalStateException {
//        Item findItemById = findItemById(id);
//
//        //아이템 정보 업데이트
//        findItemById.updateItem(name, price, stock);
//    }

//    private void validateDuplicateItem(String newItemName) throws IllegalStateException {
//        Optional<Item> itemFindByNewItemName = itemRepository.findItemByItemName(newItemName);
//
//        //같은 이름의 아이템이 있으면 예외 발생
//        itemFindByNewItemName.ifPresent(m -> {
//            throw new IllegalStateException("이미 존재하는 아이템입니다.");
//        });
//    }
}
