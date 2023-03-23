package site.mylittlestore.dto.orderitem;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.mylittlestore.domain.item.Item;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderItemCreationDto {

    private Long id;
    private Long orderId;
    private Item item;
    private int price;
    private int count;
    private LocalDateTime time;

    @Builder
    @QueryProjection
    public OrderItemCreationDto(Long id, Long orderId, Item item, int price, int count, LocalDateTime time) {
        this.id = id;
        this.orderId = orderId;
        this.item = item;
        this.price = price;
        this.count = count;
        this.time = time;
    }
}
