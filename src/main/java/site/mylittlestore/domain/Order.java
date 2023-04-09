package site.mylittlestore.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import site.mylittlestore.dto.order.OrderDto;
import site.mylittlestore.enumstorage.status.OrderStatus;
import site.mylittlestore.entity.BaseEntity;

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

    @OneToOne(mappedBy = "order")
    private StoreTable storeTable;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems;

    @OneToOne(mappedBy = "order")
    private Payment payment;

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

    //== 연관관계 메소드 ==//
    public void createPayment(Payment payment) {
        this.orderStatus = OrderStatus.IN_PROGRESS;
        this.payment = payment;
    }

    //== 테스트 로직 ==//
    public void changeOrderStatusDeleted() {
        this.orderStatus = OrderStatus.DELETED;
    }

    public void changeOrderStatusUsing() {
        this.orderStatus = OrderStatus.USING;
    }

    public void changeOrderStatusInProgress() {
        this.orderStatus = OrderStatus.IN_PROGRESS;
    }

    public void changeOrderStatusPaid() {
        this.orderStatus = OrderStatus.PAID;
    }

    //== DTO ==//

    public OrderDto toOrderDto() {
        return OrderDto.builder()
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
