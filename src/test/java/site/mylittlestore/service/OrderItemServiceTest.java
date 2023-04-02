package site.mylittlestore.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import site.mylittlestore.domain.Address;
import site.mylittlestore.domain.OrderItem;
import site.mylittlestore.dto.item.ItemCreationDto;
import site.mylittlestore.dto.item.ItemFindDto;
import site.mylittlestore.dto.member.MemberCreationDto;
import site.mylittlestore.dto.orderitem.OrderItemCreationDto;
import site.mylittlestore.dto.orderitem.OrderItemDto;
import site.mylittlestore.dto.orderitem.OrderItemFindDtoWithItemFindDto;
import site.mylittlestore.dto.orderitem.OrderItemFindDto;
import site.mylittlestore.dto.store.StoreDtoWithStoreTableFindDtosAndItemFindDtos;
import site.mylittlestore.dto.store.StoreUpdateDto;
import site.mylittlestore.enumstorage.errormessage.OrderItemErrorMessage;
import site.mylittlestore.enumstorage.status.OrderItemStatus;
import site.mylittlestore.exception.item.NotEnoughStockException;
import site.mylittlestore.exception.orderitem.OrderItemException;
import site.mylittlestore.exception.store.StoreClosedException;
import site.mylittlestore.repository.orderitem.OrderItemRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(scripts = {"classpath:sql/test.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class OrderItemServiceTest {
    @Autowired
    private MemberService memberService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private StoreTableService storeTableService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private ItemService itemService;
    
    @Autowired
    OrderItemRepository orderItemRepository;

    private Long memberTestId;
    private Long storeTestId;
    private Long itemTestId;
    private Long storeTableTestId;
    private Long orderTestId;

    @BeforeEach
    void setUp() {
        Long newMemberId = memberService.joinMember(MemberCreationDto.builder()
                .name("memberTest")
                .email("memberTest@gmail.com")
                .password("password")
                .address(Address.builder()
                        .city("city")
                        .street("street")
                        .zipcode("zipcode")
                        .build())
                .build());

        Long newStoreId = memberService.createStore(StoreDtoWithStoreTableFindDtosAndItemFindDtos.builder()
                .memberId(newMemberId)
                .name("storeTest")
                .address(Address.builder()
                        .city("city")
                        .street("street")
                        .zipcode("zipcode")
                        .build())
                .build());

        Long newItemId = storeService.createItem(ItemCreationDto.builder()
                .storeId(newStoreId)
                .name("itemTest")
                .price(10000L)
                .stock(100L)
                .build());

        //가게 열기
        memberService.changeStoreStatus(StoreUpdateDto.builder()
                .id(newStoreId)
                .memberId(newMemberId)
                .build());

        //테이블 생성
        Long createdStoreTableId = storeTableService.createStoreTable(newStoreId);

        //주문 생성
        Long createdOrderId = orderService.createOrder(newStoreId, createdStoreTableId);

        memberTestId = newMemberId;
        storeTestId = newStoreId;
        itemTestId = newItemId;
        storeTableTestId = createdStoreTableId;
        orderTestId = createdOrderId;
    }

    @Test
    @DisplayName("주문 Id로 주문된 주문 상품을 모두 찾는다.")
    void findAllOrderItemDtoWithItemNameDtoByOrderId() {
        //given
        //ORDERED도 넣어보고
        //다른 상태도 넣어보고
        //다른 주문에 ORDERED도 넣어보고

        //when


        //then
        //정확히 잘 찾는지 확인
        assertThat(1).isEqualTo(2);
    }

    @Test
    void findAllOrderItemByOrderId() {
        //given

        Long createdOrderItemId1 = orderItemService.createOrderItem(OrderItemCreationDto.builder()
                .orderId(orderTestId)
                .itemId(itemTestId)
                .price(10000L)
                .count(1L)
                .build());

        Long createdOrderItemId2 = orderItemService.createOrderItem(OrderItemCreationDto.builder()
                .orderId(orderTestId)
                .itemId(itemTestId)
                .price(5000L)
                .count(5L)
                .build());

        //when
        List<OrderItemFindDto> findAllOrderItemByOrderId = orderItemService.findAllOrderItemByOrderId(orderTestId);

        //then
        assertThat(findAllOrderItemByOrderId.size()).isEqualTo(2);

        //상품 재고 어떻게 되는지도 확인
        ItemFindDto itemFindDto = itemService.findItemDtoById(itemTestId);
        assertThat(itemFindDto.getStock()).isEqualTo(94);
    }

    @Test
    void createOrderItem() {
        //given
        //상품 추가
        Long newItemId = storeService.createItem(ItemCreationDto.builder()
                .storeId(storeTestId)
                .name("itemTest2")
                .price(9999L)
                .stock(99L)
                .build());

        //주문 상품 생성
        Long createdOrderItemId1 = orderItemService.createOrderItem(OrderItemCreationDto.builder()
                .orderId(orderTestId)
                .itemId(itemTestId)
                .price(10000L)
                .count(1L)
                .build());

        Long createdOrderItemId2 = orderItemService.createOrderItem(OrderItemCreationDto.builder()
                .orderId(orderTestId)
                .itemId(itemTestId)
                .price(9999L)
                .count(10L)
                .build());

        //when
        //생성된 주문 상품 조회
        OrderItemFindDtoWithItemFindDto findOrderItemById1 = orderItemService.findOrderItemDtoByIdWithItemFindDto(createdOrderItemId1, orderTestId);
        OrderItemFindDtoWithItemFindDto findOrderItemById2 = orderItemService.findOrderItemDtoByIdWithItemFindDto(createdOrderItemId2, orderTestId);

        //then
        assertThat(findOrderItemById1.getItemFindDto().getId()).isEqualTo(itemTestId);
        assertThat(findOrderItemById1.getPrice()).isEqualTo(10000L);
        assertThat(findOrderItemById1.getCount()).isEqualTo(1);

        assertThat(findOrderItemById2.getItemFindDto().getId()).isEqualTo(itemTestId);
        assertThat(findOrderItemById2.getPrice()).isEqualTo(9999L);
        assertThat(findOrderItemById2.getCount()).isEqualTo(10);

        //주문 상품 개수 확인
        List<OrderItemFindDto> findAllOrderItemByOrderId = orderItemService.findAllOrderItemByOrderId(orderTestId);
        assertThat(findAllOrderItemByOrderId.size()).isEqualTo(2);

        //재고 관련 확인
        ItemFindDto itemfindDto = itemService.findItemDtoById(itemTestId);
        assertThat(itemfindDto.getStock()).isEqualTo(89);

        ItemFindDto newItemFindDto = itemService.findItemDtoById(newItemId);
        assertThat(newItemFindDto.getStock()).isEqualTo(99);
    }

    @Test
    @DisplayName("주문 상품 생성 시 같은 상품, 같은 가격일 경우")
    void createOrderItemWithSameItemIdAndSameItemPrice() {
        //given
        //주문 상품 생성
        Long createdOrderItemId1 = orderItemService.createOrderItem(OrderItemCreationDto.builder()
                .orderId(orderTestId)
                .itemId(itemTestId)
                .price(10000L)
                .count(99L)
                .build());

        //같은 상품으로 주문 상품 생성
        Long createdOrderItemId2 = orderItemService.createOrderItem(OrderItemCreationDto.builder()
                .orderId(orderTestId)
                .itemId(itemTestId)
                .price(10000L)
                .count(1L)
                .build());

        //when
        //생성된 주문 상품 조회
        OrderItemFindDtoWithItemFindDto findOrderItemById1 = orderItemService.findOrderItemDtoByIdWithItemFindDto(createdOrderItemId1, orderTestId);

        //then
        assertThat(findOrderItemById1.getItemFindDto().getId()).isEqualTo(itemTestId);
        assertThat(findOrderItemById1.getPrice()).isEqualTo(10000L);
        assertThat(findOrderItemById1.getCount()).isEqualTo(100);

        //주문 상품 개수 확인
        List<OrderItemFindDto> findAllOrderItemByOrderId = orderItemService.findAllOrderItemByOrderId(orderTestId);
        assertThat(findAllOrderItemByOrderId.size()).isEqualTo(1);
        assertThat(createdOrderItemId1).isEqualTo(createdOrderItemId2);

        //재고 관련 확인
        ItemFindDto itemFindDto = itemService.findItemDtoById(itemTestId);
        assertThat(itemFindDto.getStock()).isEqualTo(0);
    }

    @Test
    @DisplayName("id는 같지만 가격이 다른 경우 테스트")
    void createOrderItemWithSameIdButDifferentPrice() {
        //given
        //주문 상품 생성
        Long createdOrderItemId1 = orderItemService.createOrderItem(OrderItemCreationDto.builder()
                .orderId(orderTestId)
                .itemId(itemTestId)
                .price(10000L)
                .count(1L)
                .build());

        //같은 상품으로 주문 상품 생성
        Long createdOrderItemId2 = orderItemService.createOrderItem(OrderItemCreationDto.builder()
                .orderId(orderTestId)
                .itemId(itemTestId)
                .price(8000L)
                .count(1L)
                .build());

        //when
        //생성된 주문 상품 조회
        OrderItemFindDtoWithItemFindDto findOrderItemById1 = orderItemService.findOrderItemDtoByIdWithItemFindDto(createdOrderItemId1, orderTestId);
        OrderItemFindDtoWithItemFindDto findOrderItemById2 = orderItemService.findOrderItemDtoByIdWithItemFindDto(createdOrderItemId2, orderTestId);

        //then
        assertThat(findOrderItemById1.getItemFindDto().getId()).isEqualTo(itemTestId);
        assertThat(findOrderItemById1.getPrice()).isEqualTo(10000L);
        assertThat(findOrderItemById1.getCount()).isEqualTo(1);

        assertThat(findOrderItemById2.getItemFindDto().getId()).isEqualTo(itemTestId);
        assertThat(findOrderItemById2.getPrice()).isEqualTo(8000L);
        assertThat(findOrderItemById2.getCount()).isEqualTo(1);

        //주문 상품 개수 확인
        List<OrderItemFindDto> findAllOrderItemByOrderId = orderItemService.findAllOrderItemByOrderId(orderTestId);
        assertThat(findAllOrderItemByOrderId.size()).isEqualTo(2);
        assertThat(createdOrderItemId1).isNotEqualTo(createdOrderItemId2);

        //재고 관련 확인
        ItemFindDto itemFindDto = itemService.findItemDtoById(itemTestId);
        assertThat(itemFindDto.getStock()).isEqualTo(98);
    }

    @Test
    void createOrderItemNotEnoughStockException() {
        //given
        //주문 상품 생성
        Long createdOrderItemId1 = orderItemService.createOrderItem(OrderItemCreationDto.builder()
                .orderId(orderTestId)
                .itemId(itemTestId)
                .price(10000L)
                .count(50L)
                .build());

        Long createdOrderItemId2 = orderItemService.createOrderItem(OrderItemCreationDto.builder()
                .orderId(orderTestId)
                .itemId(itemTestId)
                .price(8000L)
                .count(50L)
                .build());

        //when
        //생성된 주문 상품 조회
        OrderItemFindDtoWithItemFindDto findOrderItemById1 = orderItemService.findOrderItemDtoByIdWithItemFindDto(createdOrderItemId1, orderTestId);
        OrderItemFindDtoWithItemFindDto findOrderItemById2 = orderItemService.findOrderItemDtoByIdWithItemFindDto(createdOrderItemId2, orderTestId);

        //then
        assertThat(findOrderItemById1.getItemFindDto().getId()).isEqualTo(itemTestId);
        assertThat(findOrderItemById1.getPrice()).isEqualTo(10000L);
        assertThat(findOrderItemById1.getCount()).isEqualTo(50);

        assertThat(findOrderItemById2.getItemFindDto().getId()).isEqualTo(itemTestId);
        assertThat(findOrderItemById2.getPrice()).isEqualTo(8000L);
        assertThat(findOrderItemById2.getCount()).isEqualTo(50);

        //주문 상품 개수 확인
        List<OrderItemFindDto> findAllOrderItemByOrderId = orderItemService.findAllOrderItemByOrderId(orderTestId);
        assertThat(findAllOrderItemByOrderId.size()).isEqualTo(2);
        assertThat(createdOrderItemId1).isNotEqualTo(createdOrderItemId2);

        //재고 관련 확인
        ItemFindDto itemFindDto = itemService.findItemDtoById(itemTestId);
        assertThat(itemFindDto.getStock()).isEqualTo(0);

        //then
        //재고 부족 예외 발생
        assertThatThrownBy(() -> orderItemService.createOrderItem(OrderItemCreationDto.builder()
                .orderId(orderTestId)
                .itemId(itemTestId)
                .price(8000L)
                .count(1L)
                .build()))
        .isInstanceOf(NotEnoughStockException.class);
    }

    @Test
    void createOrderItemStoreIsClosedException() {
        //when
        //가게 닫기
        memberService.changeStoreStatus(StoreUpdateDto.builder()
                .id(storeTestId)
                .memberId(memberTestId)
                .build());

        //then
        //가게 닫혔을 때 주문 생성
        assertThatThrownBy(() -> orderItemService.createOrderItem(OrderItemCreationDto.builder()
                .orderId(orderTestId)
                .itemId(itemTestId)
                .price(10000L)
                .count(50L)
                .build()))
                .isInstanceOf(StoreClosedException.class);
    }

    @Test
    @DisplayName("주문 수량 변경 테스트")
    void updateOrderItemCount() {
        //when
        //주문 생성
        Long createdOrderItemId = orderItemService.createOrderItem(OrderItemCreationDto.builder()
                .orderId(orderTestId)
                .itemId(itemTestId)
                .price(10000L)
                .count(50L)
                .build());

        //주문 수정(수량 줄이기)
        Long savedOrderItemId1 = orderItemService.updateOrderItemCount(OrderItemDto.builder()
                .id(createdOrderItemId)
                .orderId(orderTestId)
                .itemId(itemTestId)
                .price(10000L)
                .count(49L)
                .build());

        //then
        //수정된 주문 조회
        OrderItemFindDtoWithItemFindDto findOrderItemById1 = orderItemService.findOrderItemDtoByIdWithItemFindDto(savedOrderItemId1, orderTestId);
        assertThat(findOrderItemById1.getPrice()).isEqualTo(10000L);
        assertThat(findOrderItemById1.getCount()).isEqualTo(49);

        ItemFindDto findItemDtoById1 = itemService.findItemDtoById(itemTestId);
        assertThat(findItemDtoById1.getStock()).isEqualTo(51);

        //주문 수정(수량 늘리기)
        Long savedOrderItemId2 = orderItemService.updateOrderItemCount(OrderItemDto.builder()
                .id(createdOrderItemId)
                .orderId(orderTestId)
                .itemId(itemTestId)
                .price(10000L)
                .count(51L)
                .build());

        //수정된 주문 조회
        OrderItemFindDtoWithItemFindDto findOrderItemById2 = orderItemService.findOrderItemDtoByIdWithItemFindDto(savedOrderItemId2, orderTestId);
        assertThat(findOrderItemById2.getPrice()).isEqualTo(10000L);
        assertThat(findOrderItemById2.getCount()).isEqualTo(51);

        ItemFindDto findItemDtoById2 = itemService.findItemDtoById(itemTestId);
        assertThat(findItemDtoById2.getStock()).isEqualTo(49);
    }

    @Test
    void deleteOrderItem() {
        //when
        //주문 생성
        Long createdOrderItemId = orderItemService.createOrderItem(OrderItemCreationDto.builder()
                .orderId(orderTestId)
                .itemId(itemTestId)
                .price(10000L)
                .count(50L)
                .build());

        //주문 삭제
        orderItemService.deleteOrderItem(OrderItemDto.builder()
                .id(createdOrderItemId)
                .orderId(orderTestId)
                .itemId(itemTestId)
                .price(10000L)
                .count(50L)
                .build());

        //then
        //주문 삭제 확인
        assertThatThrownBy(() -> orderItemService.findOrderItemDtoById(createdOrderItemId, orderTestId)).isInstanceOf(OrderItemException.class);

        //재고 확인
        ItemFindDto findItemDtoById = itemService.findItemDtoById(itemTestId);
        assertThat(findItemDtoById.getStock()).isEqualTo(100);
        
        //주문 삭제 확인
        OrderItem orderItem = orderItemRepository.findById(createdOrderItemId)
                .orElseThrow(() -> new OrderItemException(OrderItemErrorMessage.NO_SUCH_ORDER_ITEM.getMessage(), orderTestId));
        assertThat(orderItem.getOrderItemStatus()).isEqualTo(OrderItemStatus.DELETED);
    }
    
    @Test
    @DisplayName("결제가 생기면 orderItem 추가, 수정, 삭제 불가하도록")
    void validateOrderItemChangeAbility() {
        //given
        
        
        //when
        
        
        //then
        
        assertThat(1).isEqualTo(2);
    }
}
