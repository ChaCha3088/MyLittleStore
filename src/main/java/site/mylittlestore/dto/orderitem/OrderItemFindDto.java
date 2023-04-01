package site.mylittlestore.dto.orderitem;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderItemFindDto {

    private Long id;
    private Long storeId;
    private Long orderId;
    private Long itemId;
    private String itemName;
    private Long price;
    private Long count;
    private LocalDateTime time;
    private String orderItemStatus;

    @Builder
    @QueryProjection
    public OrderItemFindDto(Long id, Long storeId, Long orderId, Long itemId, String itemName, Long price, Long count, LocalDateTime time, String orderItemStatus) {
        this.id = id;
        this.storeId = storeId;
        this.orderId = orderId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.price = price;
        this.count = count;
        this.time = time;
        this.orderItemStatus = orderItemStatus;
    }
}
