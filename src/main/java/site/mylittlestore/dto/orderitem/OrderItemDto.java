package site.mylittlestore.dto.orderitem;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderItemDto {
    private Long id;
    private Long orderId;
    private Long itemId;
    private String itemName;
    private Long price;
    private Long count;

    @Builder
    @QueryProjection
    public OrderItemDto(Long id, Long orderId, Long itemId, String itemName, Long price, Long count) {
        this.id = id;
        this.orderId = orderId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.price = price;
        this.count = count;
    }
}
