package site.mylittlestore.dto.order;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import site.mylittlestore.dto.orderitem.OrderItemDtoWithItemFindDto;
import site.mylittlestore.enumstorage.status.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderDtoWithOrderItemDto {

    private Long id;
    private Long storeId;
    private int orderNumber;
    private List<OrderItemDtoWithItemFindDto> orderItemDtoWithItemFindDtoList;
    private LocalDateTime startTime;
    private OrderStatus orderStatus;

    @Builder
    @QueryProjection
    public OrderDtoWithOrderItemDto(Long id, Long storeId, int orderNumber, List<OrderItemDtoWithItemFindDto> orderItemDtoWithItemFindDtoList, LocalDateTime startTime, OrderStatus orderStatus) {
        this.id = id;
        this.storeId = storeId;
        this.orderNumber = orderNumber;
        this.orderItemDtoWithItemFindDtoList = orderItemDtoWithItemFindDtoList;
        this.startTime = startTime;
        this.orderStatus = orderStatus;
    }
}