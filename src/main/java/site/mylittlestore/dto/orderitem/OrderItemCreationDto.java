package site.mylittlestore.dto.orderitem;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderItemCreationDto {
    private Long orderId;
    private Long itemId;
    private int price;
    private int count;

    @Builder
    @QueryProjection
    public OrderItemCreationDto(Long orderId, Long itemId, int price, int count) {
        this.orderId = orderId;
        this.itemId = itemId;
        this.price = price;
        this.count = count;
    }
}
