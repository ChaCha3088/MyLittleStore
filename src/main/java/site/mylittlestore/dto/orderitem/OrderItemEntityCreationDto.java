package site.mylittlestore.dto.orderitem;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderItemEntityCreationDto {
    private Long itemId;
    private int price;
    private int count;

    @Builder
    @QueryProjection
    public OrderItemEntityCreationDto(Long itemId, int price, int count) {
        this.itemId = itemId;
        this.price = price;
        this.count = count;
    }
}
