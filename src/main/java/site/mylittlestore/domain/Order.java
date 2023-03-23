package site.mylittlestore.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import site.mylittlestore.dto.order.OrderDtoWithOrderItemDto;
import site.mylittlestore.dto.order.OrderDtoWithOrderItemId;
import site.mylittlestore.enumstorage.status.OrderStatus;
import site.mylittlestore.dto.orderitem.OrderItemCreationDto;
import site.mylittlestore.entity.BaseEntity;
import site.mylittlestore.exception.item.NotEnoughStockException;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Table(name = "ORDERS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "ORDER_ID")
    private Long id;

    @NotNull
    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "STORE_ID")
    private Store store;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "PAYMENT_ID")
    private Payment payment;

    @OneToOne(mappedBy = "order")
    private StoreTable storeTable;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems;

    @NotNull
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Builder
    protected Order(Store store, StoreTable storeTable) {
        this.store = store;
        this.storeTable = storeTable;
        this.orderItems = new ArrayList<>();
        this.startTime = LocalDateTime.now();
        this.orderStatus = OrderStatus.EMPTY;
    }

    public OrderItem createOrderItem(OrderItemCreationDto orderItemCreationDto) throws NotEnoughStockException {
        OrderItem createdOrderItem = OrderItem.builder()
                .order(this)
                .item(orderItemCreationDto.getItem())
                .price(orderItemCreationDto.getPrice())
                .count(orderItemCreationDto.getCount())
                .build();

        createdOrderItem.setOrder(this);
        orderItems.add(createdOrderItem);

        return createdOrderItem;
    }

    //==DTO==//
    public OrderDtoWithOrderItemDto toOrderDtoWithOrderItemDto() {
        return OrderDtoWithOrderItemDto.builder()
                .id(id)
                .storeId(store.getId())
                .orderItemDtoWithItemFindDtoList(orderItems.stream()
                        .map(OrderItem::toOrderItemDtoWithItemFindDto)
                        .collect(Collectors.toList()))
                .startTime(startTime)
                .orderStatus(orderStatus)
                .build();
    }

    public OrderDtoWithOrderItemId toOrderDtoWithOrderItemId(List<Long> findAllOrderItemIdByOrderId) {
        return OrderDtoWithOrderItemId.builder()
                .id(id)
                .storeId(store.getId())
                .orderItemIdList(findAllOrderItemIdByOrderId)
                .startTime(startTime)
                .orderStatus(orderStatus)
                .build();
    }
}