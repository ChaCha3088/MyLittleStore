package site.mylittlestore.dto.orderitem;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.mylittlestore.dto.item.ItemCreationDto;
import site.mylittlestore.dto.item.ItemFindDto;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderItemDtoWithItemFindDto {

    private Long id;
    private Long storeId;
    private Long orderId;
    private ItemFindDto itemFindDto;
    private String itemName;
    private Long price;
    private Long count;
    private LocalDateTime time;
    private String orderItemStatus;

    @Builder
    @QueryProjection
    public OrderItemDtoWithItemFindDto(Long id, Long storeId, Long orderId, ItemFindDto itemFindDto, String itemName, Long price, Long count, LocalDateTime time, String orderItemStatus) {
        this.id = id;
        this.storeId = storeId;
        this.orderId = orderId;
        this.itemFindDto = itemFindDto;
        this.itemName = itemName;
        this.price = price;
        this.count = count;
        this.time = time;
        this.orderItemStatus = orderItemStatus;
    }
}
