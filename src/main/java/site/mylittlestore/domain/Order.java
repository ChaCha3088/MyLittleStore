package site.mylittlestore.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import site.mylittlestore.dto.order.OrderDtoWithOrderItemDto;
import site.mylittlestore.dto.order.OrderDtoWithOrderItemId;
import site.mylittlestore.dto.orderitem.OrderItemEntityCreationDto;
import site.mylittlestore.enumstorage.status.OrderStatus;
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

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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
        this.orderStatus = OrderStatus.USING;

        storeTable.setOrder(this);
    }

    //==DTO==//
    public OrderDtoWithOrderItemDto toOrderDtoWithOrderItemDto() {
        return OrderDtoWithOrderItemDto.builder()
                .id(id)
                .storeId(store.getId())
                .paymentId(payment != null ? payment.getId() : null)
                .storeTableId(storeTable.getId())
                .orderItemDtoWithItemFindDtos(orderItems.stream()
                        .map(OrderItem::toOrderItemDtoWithItemFindDto)
                        .collect(Collectors.toList()))
                .startTime(startTime)
                .orderStatus(orderStatus.toString())
                .build();
    }

    public OrderDtoWithOrderItemId toOrderDtoWithOrderItemId() {
        return OrderDtoWithOrderItemId.builder()
                .id(id)
                .storeId(store.getId())
                .paymentId(payment != null ? payment.getId() : null)
                .storeTableId(storeTable.getId())
                .orderItemIds(orderItems.stream()
                        .map(OrderItem::getId)
                        .collect(Collectors.toList()))
                .startTime(startTime)
                .orderStatus(orderStatus.toString())
                .build();
    }
}
