package site.mylittlestore.dto.order;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import site.mylittlestore.dto.orderitem.OrderItemFindDtoWithItemFindDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderDtoWithOrderItemDtoWithItemFindDto {

    private Long id;
    private Long storeId;
    private Long paymentId;
    private Long storeTableId;
    private List<OrderItemFindDtoWithItemFindDto> orderItemFindDtoWithItemFindDtos;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String orderStatus;

    @Builder
    @QueryProjection
    public OrderDtoWithOrderItemDtoWithItemFindDto(Long id, Long storeId, Long paymentId, Long storeTableId, List<OrderItemFindDtoWithItemFindDto> orderItemFindDtoWithItemFindDtos, LocalDateTime startTime, LocalDateTime endTime, String orderStatus) {
        this.id = id;
        this.storeId = storeId;
        this.paymentId = paymentId;
        this.storeTableId = storeTableId;
        this.orderItemFindDtoWithItemFindDtos = orderItemFindDtoWithItemFindDtos;
        this.startTime = startTime;
        this.endTime = endTime;
        this.orderStatus = orderStatus;
    }
}