package site.mylittlestore.dto.storetable;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import site.mylittlestore.dto.order.OrderDtoWithOrderItemId;

@Getter
public class StoreTableFindDtoWithOrderFindDto {
    private Long id;
    private Long storeId;
    private OrderDtoWithOrderItemId orderDtoWithOrderItemId;
    private Long xCoordinate;

    private Long yCoordinate;
    private String storeTableStatus;

    @Builder
    @QueryProjection
    public StoreTableFindDtoWithOrderFindDto(Long id, Long storeId, OrderDtoWithOrderItemId orderDtoWithOrderItemId, Long xCoordinate, Long yCoordinate, String storeTableStatus) {
        this.id = id;
        this.storeId = storeId;
        this.orderDtoWithOrderItemId = orderDtoWithOrderItemId;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.storeTableStatus = storeTableStatus;
    }
}
