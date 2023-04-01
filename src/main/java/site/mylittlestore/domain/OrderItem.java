package site.mylittlestore.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import site.mylittlestore.domain.item.Item;
import site.mylittlestore.dto.orderitem.OrderItemDtoWithItemFindDto;
import site.mylittlestore.dto.orderitem.OrderItemFindDto;
import site.mylittlestore.entity.BaseEntity;
import site.mylittlestore.enumstorage.status.OrderItemStatus;
import site.mylittlestore.exception.item.NotEnoughStockException;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ITEM_ID")
    private Long id;

    @NotNull
    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "STORE_ID")
    private Store store;

    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "ORDER_ID")
    private Order order;

    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "ITEM_ID")
    private Item item;

    @NotBlank
    private String itemName;

    @NotNull
    @Min(value = 1, message = "가격은 0보다 커야합니다.")
    private Long price;

    @NotNull
    @Min(value = 1, message = "수량은 0보다 커야합니다.")
    private Long count;

    @NotNull
    private LocalDateTime time;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderItemStatus orderItemStatus;

    @Builder
    protected OrderItem(Store store, Order order, Item item, Long price, Long count) throws NotEnoughStockException {
        this.store = store;
        this.order = order;
        this.item = item;
        this.itemName = item.getName();
        this.price = price;
        this.count = count;
        this.time = LocalDateTime.now();
        this.orderItemStatus = OrderItemStatus.ORDERED;

        //OrderItem 생성시 Item의 stock 감소
        this.item.decreaseStock(count);

        //OrderItem과 Order 연관관계 설정
        order.getOrderItems().add(this);
    }

    public Item addCount(Long count) throws NotEnoughStockException {
        this.item.decreaseStock(count);
        this.count += count;

        return this.item;
    }

    public void updatePrice(Long price) {
        this.price = price;
    }

    public void updateCount(Long count) {
        Long oldCount = this.count;

        if (oldCount > count) {
            this.item.increaseStock(oldCount - count);
        } else if (oldCount < count) {
            this.item.decreaseStock(count - oldCount);
        }

        this.count = count;
    }

    //==연관관계 메소드==//
    public void setOrder(Order order) {
        this.order = order;
    }

    //==Dto==//
    public OrderItemFindDto toOrderItemDto() {
        return OrderItemFindDto.builder()
                .id(id)
                .storeId(store.getId())
                .orderId(order.getId())
                .itemId(item.getId())
                .itemName(itemName)
                .price(price)
                .count(count)
                .time(time)
                .orderItemStatus(orderItemStatus.toString())
                .build();
    }

    public OrderItemDtoWithItemFindDto toOrderItemDtoWithItemFindDto() {
        return OrderItemDtoWithItemFindDto.builder()
                .id(id)
                .storeId(store.getId())
                .orderId(order.getId())
                .itemFindDto(item.toItemFindDto())
                .itemName(itemName)
                .price(price)
                .count(count)
                .time(time)
                .orderItemStatus(orderItemStatus.toString())
                .build();
    }
}
