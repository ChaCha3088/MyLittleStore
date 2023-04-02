package site.mylittlestore.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
public class ItemUpdateForm {

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotNull(message = "가격은 필수입니다.")
    @Min(value = 1, message = "가격은 0보다 커야합니다.")
    private Long price;

    @NotNull(message = "수량은 필수입니다.")
    @Min(value = 1, message = "수량은 0보다 커야합니다.")
    private Long stock;
}
