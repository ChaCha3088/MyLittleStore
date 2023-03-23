package site.mylittlestore.dto.orderitem;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderItemDeleteDto {

    private Long id;
    private Long storeId;
    private Long orderId;
    private Long itemId;
    private int price;
    private int count;
    private LocalDateTime time;

    @Builder
    @QueryProjection
    public OrderItemDeleteDto(Long id, Long storeId, Long orderId, Long itemId, int price, int count, LocalDateTime time) {
        this.id = id;
        this.storeId = storeId;
        this.orderId = orderId;
        this.itemId = itemId;
        this.price = price;
        this.count = count;
        this.time = time;
    }
}
