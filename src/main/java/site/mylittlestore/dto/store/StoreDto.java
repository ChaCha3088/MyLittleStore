package site.mylittlestore.dto.store;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import site.mylittlestore.domain.Address;
import site.mylittlestore.domain.Order;
import site.mylittlestore.domain.StoreTable;
import site.mylittlestore.domain.item.Item;
import site.mylittlestore.enumstorage.status.StoreStatus;

import java.util.List;

@Getter
public class StoreDto {

    private Long id;

    private Long memberId;

    private String name;

    private Address address;

    private StoreStatus storeStatus;

    private List<StoreTable> storeTables;

    private List<Item> items;

    @Builder
    @QueryProjection
    public StoreDto(Long id, Long memberId, String name, Address address, StoreStatus storeStatus, List<StoreTable> storeTables, List<Item> items) {
        this.id = id;
        this.memberId = memberId;
        this.name = name;
        this.address = address;
        this.storeStatus = storeStatus;
        this.storeTables = storeTables;
        this.items = items;
    }
}
