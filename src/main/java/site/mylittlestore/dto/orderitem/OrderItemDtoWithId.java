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
public class OrderItemDtoWithId {

    private Long id;

    @Builder
    @QueryProjection
    public OrderItemDtoWithId(Long id) {
        this.id = id;
    }
}
