package site.mylittlestore.dto.orderitem;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderItemDtoWithItemName {

    private Long id;
    private Long orderId;
    private String itemName;
    private int price;
    private int count;
    private LocalDateTime time;

    @Builder
    @QueryProjection
    public OrderItemDtoWithItemName(Long id, Long orderId, String itemName, int price, int count, LocalDateTime time) {
        this.id = id;
        this.orderId = orderId;
        this.itemName = itemName;
        this.price = price;
        this.count = count;
        this.time = time;
    }
}