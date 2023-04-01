package site.mylittlestore.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import site.mylittlestore.dto.storetable.StoreTableFindDto;
import site.mylittlestore.dto.storetable.StoreTableFindDtoWithOrderFindDto;
import site.mylittlestore.entity.BaseEntity;
import site.mylittlestore.enumstorage.status.StoreTableStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreTable extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TABLE_ID")
    private Long id;

    @NotNull
    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "STORE_ID")
    private Store store;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "ORDER_ID")
    private Order order;

    private Long xCoordinate;

    private Long yCoordinate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private StoreTableStatus storeTableStatus;

    @Builder
    protected StoreTable(Store store) {
        this.store = store;
        this.storeTableStatus = StoreTableStatus.EMPTY;
    }

    //-- 비즈니스 로직 --//
    public void delete() {
        this.storeTableStatus = StoreTableStatus.DELETED;
    }

    //-- 연관 관계 메소드 --//
    public void setOrder(Order order) {
        this.order = order;
        this.storeTableStatus = StoreTableStatus.USING;
    }

    //-- DTO 생성 메소드 --//
    public StoreTableFindDto toStoreTableFindDto() {
        return StoreTableFindDto.builder()
                .id(id)
                .storeId(store.getId())
                .orderId(order != null ? order.getId() : null)
                .xCoordinate(xCoordinate)
                .yCoordinate(yCoordinate)
                .storeTableStatus(storeTableStatus.toString())
                .build();
    }

    public StoreTableFindDtoWithOrderFindDto toStoreTableFindDtoWithOrderFindDto() {
        return StoreTableFindDtoWithOrderFindDto.builder()
                .id(id)
                .storeId(store.getId())
                .orderDtoWithOrderItemId(order != null ? order.toOrderDtoWithOrderItemId() : null)
                .xCoordinate(xCoordinate)
                .yCoordinate(yCoordinate)
                .storeTableStatus(storeTableStatus.toString())
                .build();
    }
}
