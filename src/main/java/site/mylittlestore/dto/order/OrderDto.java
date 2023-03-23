package site.mylittlestore.dto.order;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import site.mylittlestore.dto.orderitem.OrderItemFindDto;
import site.mylittlestore.enumstorage.status.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderDto {

    private Long id;
    private Long storeId;
    private int orderNumber;
    private List<OrderItemFindDto> orderItemFindDtoList;
    private LocalDateTime startTime;
    private OrderStatus tableStatus;

    @Builder
    @QueryProjection
    public OrderDto(Long id, Long storeId, int orderNumber, List<OrderItemFindDto> orderItemFindDtoList, LocalDateTime startTime, OrderStatus tableStatus) {
        this.id = id;
        this.storeId = storeId;
        this.orderNumber = orderNumber;
        this.orderItemFindDtoList = orderItemFindDtoList;
        this.startTime = startTime;
        this.tableStatus = tableStatus;
    }
}
