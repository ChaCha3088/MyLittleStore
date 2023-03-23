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
    private Long orderId;
    private ItemFindDto itemFindDto;
    private int price;
    private int count;
    private LocalDateTime time;

    @Builder
    @QueryProjection
    public OrderItemDtoWithItemFindDto(Long id, Long orderId, ItemFindDto itemFindDto, int price, int count, LocalDateTime time) {
        this.id = id;
        this.orderId = orderId;
        this.itemFindDto = itemFindDto;
        this.price = price;
        this.count = count;
        this.time = time;
    }
}
