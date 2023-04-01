package site.mylittlestore.dto.store;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import site.mylittlestore.domain.Address;
import site.mylittlestore.domain.StoreTable;
import site.mylittlestore.domain.item.Item;
import site.mylittlestore.dto.address.AddressDto;
import site.mylittlestore.dto.item.ItemFindDto;
import site.mylittlestore.dto.storetable.StoreTableFindDto;
import site.mylittlestore.enumstorage.status.StoreStatus;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class StoreDtoWithStoreTableFindDtosAndItemFindDtos {

    private Long id;

    private Long memberId;

    private String name;

    private AddressDto addressDto;

    private String storeStatus;

    private List<StoreTableFindDto> storeTableFindDtos;

    private List<ItemFindDto> itemFindDtos;

    @Builder
    @QueryProjection
    public StoreDtoWithStoreTableFindDtosAndItemFindDtos(Long id, Long memberId, String name, Address address, StoreStatus storeStatus, List<StoreTable> storeTables, List<Item> items) {
        this.id = id;
        this.memberId = memberId;
        this.name = name;
        this.addressDto = AddressDto.builder()
                .city(address.getCity())
                .street(address.getStreet())
                .zipcode(address.getZipcode())
                .build();
        this.storeStatus = storeStatus.toString();
        this.storeTableFindDtos = storeTables.stream()
                .map(storeTable -> storeTable.toStoreTableFindDto())
                .collect(Collectors.toList());
        this.itemFindDtos = items.stream()
                .map(item -> item.toItemFindDto())
                .collect(Collectors.toList());
    }
}
