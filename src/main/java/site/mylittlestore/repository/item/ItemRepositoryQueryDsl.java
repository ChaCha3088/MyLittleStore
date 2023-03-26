package site.mylittlestore.repository.item;

import site.mylittlestore.domain.item.Item;
import site.mylittlestore.dto.item.ItemCreationDto;
import site.mylittlestore.dto.item.ItemFindDto;

import java.util.List;
import java.util.Optional;

public interface ItemRepositoryQueryDsl {
    Optional<ItemFindDto> findItemDtoById(Long id);
    Optional<Item> findItemByIdAndStoreId(Long id, Long storeId);


    List<ItemFindDto> findAllItemDtoByStoreId(Long storeId);

    void deleteById(Long id);
}
