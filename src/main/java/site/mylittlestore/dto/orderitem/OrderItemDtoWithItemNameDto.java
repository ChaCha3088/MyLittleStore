package site.mylittlestore.dto.orderitem;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderItemDtoWithItemNameDto {

    private Long id;
    private Long storeId;
    private Long orderId;
    private String itemName;
    private int price;
    private int count;
    private LocalDateTime time;
    private String orderItemStatus;

    @Builder
    @QueryProjection
    public OrderItemDtoWithItemNameDto(Long id, Long storeId, Long orderId, String itemName, int price, int count, LocalDateTime time, String orderItemStatus) {
        this.id = id;
        this.storeId = storeId;
        this.orderId = orderId;
        this.itemName = itemName;
        this.price = price;
        this.count = count;
        this.time = time;
        this.orderItemStatus = orderItemStatus;
    }
}