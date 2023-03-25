package site.mylittlestore.dto.order;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderFindDto {
    private Long id;

    private Long storeId;

    private Long paymentId;

    private Long storeTableId;

    private List<Long> orderItemIds;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String orderStatus;

    @Builder
    protected OrderFindDto(Long id, Long storeId, Long paymentId, Long storeTableId, List<Long> orderItemIds, LocalDateTime startTime, LocalDateTime endTime, String orderStatus) {
        this.id = id;
        this.storeId = storeId;
        this.paymentId = paymentId;
        this.storeTableId = storeTableId;
        this.orderItemIds = orderItemIds;
        this.startTime = startTime;
        this.endTime = endTime;
        this.orderStatus = orderStatus;
    }
}
