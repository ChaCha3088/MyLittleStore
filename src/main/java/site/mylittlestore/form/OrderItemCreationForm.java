package site.mylittlestore.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class OrderItemCreationForm {
    @NotNull(message = "상품 선택은 필수입니다.")
    private Long itemId;

    @NotNull(message = "가격은 필수입니다.")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private Integer price;

    @NotNull(message = "수량은 필수입니다.")
    @Min(value = 0, message = "수량은 0 이상이어야 합니다.")
    private Integer count;
}