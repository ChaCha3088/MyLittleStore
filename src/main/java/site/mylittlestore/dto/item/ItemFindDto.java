package site.mylittlestore.dto.item;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ItemFindDto {

    private Long id;

    private Long storeId;

    private String name;

    private int price;

    private int stock;

    private String image;

    @Builder
    @QueryProjection
    public ItemFindDto(Long id, Long storeId, String name, int price, int stock, String image) {
        this.id = id;
        this.storeId = storeId;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.image = image;
    }
}
