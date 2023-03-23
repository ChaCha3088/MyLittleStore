package site.mylittlestore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.mylittlestore.domain.OrderItem;
import site.mylittlestore.dto.orderitem.OrderItemFindDto;
import site.mylittlestore.dto.orderitem.OrderItemDtoWithItemFindDto;
import site.mylittlestore.dto.orderitem.OrderItemDtoWithItemName;
import site.mylittlestore.enumstorage.errormessage.OrderItemErrorMessage;
import site.mylittlestore.exception.orderitem.NoSuchOrderItemException;
import site.mylittlestore.repository.orderitem.OrderItemRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public OrderItemFindDto findOrderItemDtoById(Long orderItemId) {
        //주문이 없으면 예외 발생
        //Dto로 변환
        return orderItemRepository.findById(orderItemId).orElseThrow(() -> new NoSuchOrderItemException(OrderItemErrorMessage.NO_SUCH_ORDER_ITEM.getMessage())).toOrderItemDto();
    }

    public OrderItemDtoWithItemFindDto findOrderItemByIdWithItemFindDto(Long orderItemId) throws NoSuchOrderItemException {
        Optional<OrderItem> findOrderItemById = orderItemRepository.findByIdWithItem(orderItemId);

        //주문 상품이 없으면 예외 발생
        //Dto로 변환
        return findOrderItemById.orElseThrow(() -> new NoSuchOrderItemException(OrderItemErrorMessage.NO_SUCH_ORDER_ITEM.getMessage())).toOrderItemDtoWithItemFindDto();
    }

    public List<OrderItemDtoWithItemName> findAllOrderItemDtoWithItemNameByOrderIdOrderByTime(Long orderId) {
        List<OrderItem> findOrderItemByOrderId = orderItemRepository.findAllOrderItemByOrderIdOrderByTime(orderId);

        //Dto로 변환
        return findOrderItemByOrderId.stream().map(OrderItem::toOrderItemDtoWithItemName).collect(Collectors.toList());
    }
}
