package site.mylittlestore.domain.item;

import lombok.*;
import site.mylittlestore.domain.Store;
import site.mylittlestore.dto.item.ItemFindDto;
import site.mylittlestore.entity.BaseEntity;
import site.mylittlestore.enumstorage.errormessage.ItemErrorMessage;
import site.mylittlestore.exception.item.NotEnoughStockException;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static javax.persistence.FetchType.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "ITEMTYPE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "ITEM_ID")
    private Long id;

    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "STORE_ID")
    private Store store;

    @NotBlank
    private String name;

    @NotNull
    private int price;

    @NotNull
    private int stock;

    /**
     * 이미지는 url로 저장
     * url이 없는 경우, ""로 저장
     */
    @NotNull
    private String image;

    @Builder
    protected Item(Store store, String name, int price, int stock, String image) {
        this.store = store;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.image = "";
    }

    public void updateName(String newName) {
        this.name = newName;
    }

    public void updatePrice(int newPrice) {
        this.price = newPrice;
    }

    public void updateStock(int newStock) {
        this.stock = newStock;
    }

    //==연관관계 메소드==//
    public void setStore(Store store) {
        this.store = store;
    }

    public void increaseStock(int count) {
        this.stock += count;
    }

    public void decreaseStock(int count) throws NotEnoughStockException {
        if (this.stock < count) {
            throw new NotEnoughStockException(ItemErrorMessage.NOT_ENOUGH_STOCK.getMessage());
        }
        this.stock -= count;
    }

    //==DTO==//
    public ItemFindDto toItemDto() {
        return ItemFindDto.builder()
                .id(id)
                .storeId(store.getId())
                .name(name)
                .price(price)
                .stock(stock)
                .image(image)
                .build();
    }
}
