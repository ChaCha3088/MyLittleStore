package site.mylittlestore.dto.order;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import site.mylittlestore.enumstorage.status.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderDtoWithOrderItemId {

    private Long id;
    private Long storeId;
    private int orderNumber;
    private List<Long> orderItemList;
    private LocalDateTime startTime;
    private OrderStatus orderStatus;

    @Builder
    @QueryProjection
    public OrderDtoWithOrderItemId(Long id, Long storeId, int orderNumber, List<Long> orderItemIdList, LocalDateTime startTime, OrderStatus orderStatus) {
        this.id = id;
        this.storeId = storeId;
        this.orderNumber = orderNumber;
        this.orderItemList = orderItemIdList;
        this.startTime = startTime;
        this.orderStatus = orderStatus;
    }
}
