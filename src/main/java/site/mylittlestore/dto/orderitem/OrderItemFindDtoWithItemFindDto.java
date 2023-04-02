package site.mylittlestore.dto.orderitem;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.mylittlestore.dto.item.ItemFindDto;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderItemFindDtoWithItemFindDto {

    private Long id;
    private Long storeId;
    private Long orderId;
    private ItemFindDto itemFindDto;
    private String itemName;
    private Long price;
    private Long count;
    private LocalDateTime orderedTime;
    private LocalDateTime updatedTime;
    private String orderItemStatus;

    @Builder
    @QueryProjection
    public OrderItemFindDtoWithItemFindDto(Long id, Long storeId, Long orderId, ItemFindDto itemFindDto, String itemName, Long price, Long count, LocalDateTime orderedTime, LocalDateTime updatedTime, String orderItemStatus) {
        this.id = id;
        this.storeId = storeId;
        this.orderId = orderId;
        this.itemFindDto = itemFindDto;
        this.itemName = itemName;
        this.price = price;
        this.count = count;
        this.orderedTime = orderedTime;
        this.updatedTime = updatedTime;
        this.orderItemStatus = orderItemStatus;
    }
}
