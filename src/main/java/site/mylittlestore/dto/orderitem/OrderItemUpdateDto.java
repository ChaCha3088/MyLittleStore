package site.mylittlestore.dto.orderitem;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderItemUpdateDto {

    private Long storeId;
    private Long orderId;
    private Long itemId;
    private int price;
    private int count;

    @Builder
    @QueryProjection
    public OrderItemUpdateDto(Long storeId, Long orderId, Long itemId, int price, int count) {
        this.storeId = storeId;
        this.orderId = orderId;
        this.itemId = itemId;
        this.price = price;
        this.count = count;
    }
}
