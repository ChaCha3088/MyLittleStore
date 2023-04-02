package site.mylittlestore.dto.payment;

import lombok.Builder;
import lombok.Getter;
import site.mylittlestore.dto.order.OrderDto;
import site.mylittlestore.dto.orderitem.OrderItemFindDto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
public class PaymentViewDto {
    @NotNull
    private Long id;

    @NotNull
    private OrderDto orderDto;

    @NotEmpty
    private List<OrderItemFindDto> orderItemFindDtos;

    @NotNull
    @Min(value = 1, message = "가격은 0보다 커야합니다.")
    private Long initialPaymentAmount;

    @Builder
    protected PaymentViewDto(Long id, OrderDto orderDto, List<OrderItemFindDto> orderItemFindDtos, Long initialPaymentAmount) {
        this.id = id;
        this.orderDto = orderDto;
        this.orderItemFindDtos = orderItemFindDtos;
        this.initialPaymentAmount = initialPaymentAmount;
    }
}
