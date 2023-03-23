package site.mylittlestore.dto.store;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import site.mylittlestore.domain.Address;
import site.mylittlestore.enumstorage.status.StoreStatus;

import java.util.List;

@Getter
public class StoreOnlyDto {

    private Long id;

    private Long memberId;

    private String name;

    private Address address;

    private StoreStatus storeStatus;

    private List<Long> storeTables;

    private List<Long> items;

    @Builder
    @QueryProjection
    public StoreOnlyDto(Long id, Long memberId, String name, Address address, StoreStatus storeStatus, List<Long> storeTables, List<Long> items, int tableNumbers) {
        this.id = id;
        this.memberId = memberId;
        this.name = name;
        this.address = address;
        this.storeStatus = storeStatus;
        this.storeTables = storeTables;
        this.items = items;
    }
}
