package site.mylittlestore.dto.store;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.mylittlestore.domain.Address;
import site.mylittlestore.domain.Order;
import site.mylittlestore.domain.item.Item;
import site.mylittlestore.enumstorage.status.StoreStatus;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StoreUpdateDto {

    private Long id;

    private Long memberId;

    private String originalName;

    private String newName;

    private Address originalAddress;

    private Address newAddress;

    private StoreStatus storeStatus;

    private List<Order> orders;

    private List<Item> items;

    private int tableNumbers;

    @Builder
    @QueryProjection
    public StoreUpdateDto(Long id, Long memberId, String originalName, String newName, Address address, Address newAddress, StoreStatus storeStatus, List<Order> orders, List<Item> items, int tableNumbers) {
        this.id = id;
        this.memberId = memberId;
        this.originalName = originalName;
        this.newName = newName;
        this.originalAddress = address;
        this.newAddress = newAddress;
        this.storeStatus = storeStatus;
        this.orders = orders;
        this.items = items;
        this.tableNumbers = tableNumbers;
    }
}
