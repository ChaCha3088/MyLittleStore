package site.mylittlestore.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import site.mylittlestore.domain.Address;
import site.mylittlestore.dto.item.ItemCreationDto;
import site.mylittlestore.dto.item.ItemFindDto;
import site.mylittlestore.dto.orderitem.OrderItemDtoWithItemFindDto;
import site.mylittlestore.dto.orderitem.OrderItemFindDto;
import site.mylittlestore.dto.orderitem.OrderItemUpdateDto;
import site.mylittlestore.dto.order.OrderDtoWithOrderItemDto;
import site.mylittlestore.dto.order.OrderDtoWithOrderItemId;
import site.mylittlestore.dto.member.MemberCreationDto;
import site.mylittlestore.dto.store.StoreDto;
import site.mylittlestore.dto.store.StoreUpdateDto;
import site.mylittlestore.dto.store.StoreTableCreationDto;
import site.mylittlestore.dto.storetable.StoreTableFindDtoWithOrderFindDto;
import site.mylittlestore.enumstorage.status.OrderStatus;
import site.mylittlestore.exception.orderitem.NoSuchOrderItemException;
import site.mylittlestore.exception.store.NoSuchOrderException;
import site.mylittlestore.exception.item.NotEnoughStockException;
import site.mylittlestore.exception.store.StoreClosedException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class OrderServiceTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private StoreTableService storeTableService;
    @Autowired
    private StoreService storeService;

    @PersistenceContext
    EntityManager em;

    private Long memberTestId;
    private Long storeTestId;
    private Long itemTestId;
    private Long storeTableTestId;
    private Long orderTestId;

    @BeforeAll
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

        Long newStoreId = memberService.createStore(StoreDto.builder()
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
                .price(10000)
                .stock(100)
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
    void findOrderById() {
        //when
        OrderDtoWithOrderItemId findOrderWithOrderItemIdById = orderService.findOrderDtoWithOrderItemIdById(orderTestId);

        //then
        assertThat(findOrderWithOrderItemIdById.getOrderStatus()).isEqualTo(OrderStatus.USING);
    }

    @Test
    void findOrderByIdNoSuchOrderException() {
        //then
        assertThatThrownBy(() -> orderService.findOrderDtoWithOrderItemIdById(100L))
                .isInstanceOf(NoSuchOrderException.class);
    }

    @Test
    void findOrderDtoById() {
        //given
        //가게 열기
        memberService.changeStoreStatus(StoreUpdateDto.builder()
                .id(storeTestId)
                .memberId(memberTestId)
                .build());

        orderItemService.createOrderItem(orderTestId, itemTestId, 10000, 1);

        //영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        //when
        OrderDtoWithOrderItemDto OrderDtoWithOrderItemDtoById = orderService.findOrderDtoById(orderTestId);

        //then
        assertThat(OrderDtoWithOrderItemDtoById.getOrderStatus()).isEqualTo(OrderStatus.USING);
        assertThat(OrderDtoWithOrderItemDtoById.getOrderItemDtoWithItemFindDtos().size()).isEqualTo(1);
        assertThat(OrderDtoWithOrderItemDtoById.getOrderItemDtoWithItemFindDtos().get(0).getPrice()).isEqualTo(10000);
        assertThat(OrderDtoWithOrderItemDtoById.getOrderItemDtoWithItemFindDtos().get(0).getItemFindDto().getName()).isEqualTo("itemTest");
    }

    @Test
    void findAllOrderItemByOrderId() {
        //given
        //가게 열기
        memberService.changeStoreStatus(StoreUpdateDto.builder()
                .id(storeTestId)
                .memberId(memberTestId)
                .build());

        Long createdOrderItemId1 = orderItemService.createOrderItem(orderTestId, itemTestId, 10000, 1);

        Long createdOrderItemId2 = orderItemService.createOrderItem(orderTestId, itemTestId, 5000, 5);

        //영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        //when
        List<OrderItemFindDto> findAllOrderItemByOrderId = orderService.findAllOrderItemByOrderId(orderTestId);

        //then
        assertThat(findAllOrderItemByOrderId.size()).isEqualTo(2);

        //영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        //상품 재고 어떻게 되는지도 확인
        ItemFindDto itemFindDto = itemService.findItemDtoById(itemTestId);
        assertThat(itemFindDto.getStock()).isEqualTo(94);
    }
    
    @Test
    @DisplayName("주문 생성")
    void createOrder() {
        //when
        //테이블 생성
        Long createdStoreTableId = storeTableService.createStoreTable(storeTestId);

        //주문 생성
        Long createdOrderId = orderService.createOrder(storeTestId, createdStoreTableId);

        //영속성 컨텍스트 초기화
        em.flush();
        em.clear();
        
        //then
        StoreTableFindDtoWithOrderFindDto storeTableFindDtoWithOrderFindDtoByStoreId = storeTableService.findStoreTableFindDtoWithOrderFindDtoByStoreId(storeTableTestId, storeTestId);
        assertThat(storeTableFindDtoWithOrderFindDtoByStoreId.getOrderDtoWithOrderItemId().getOrderStatus()).isEqualTo(OrderStatus.USING.toString());
    }

    @Test
    void updateOrderItem() {
        //given
        //가게 열기
        memberService.changeStoreStatus(StoreUpdateDto.builder()
                .id(storeTestId)
                .memberId(memberTestId)
                .build());

        //영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        //when
        //주문 생성
        Long createdOrderItemId = orderItemService.createOrderItem(orderTestId, itemTestId, 10000, 50);

        //영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        //주문 수정(수량 줄이기)
        Long savedOrderItemId1 = orderService.updateOrderItem(OrderItemUpdateDto.builder()
                .storeId(storeTestId)
                .orderId(orderTestId)
                .itemId(itemTestId)
                .price(9999)
                .count(49)
                .build());

        //영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        //then
        //수정된 주문 조회
        OrderItemDtoWithItemFindDto findOrderItemById1 = orderItemService.findOrderItemByIdWithItemFindDto(savedOrderItemId1);
        assertThat(findOrderItemById1.getPrice()).isEqualTo(9999);
        assertThat(findOrderItemById1.getCount()).isEqualTo(49);

        ItemFindDto findItemDtoById1 = itemService.findItemDtoById(itemTestId);
        assertThat(findItemDtoById1.getStock()).isEqualTo(51);

        //주문 수정(수량 늘리기)
        Long savedOrderItemId2 = orderService.updateOrderItem(OrderItemUpdateDto.builder()
                .storeId(storeTestId)
                .orderId(orderTestId)
                .itemId(itemTestId)
                .price(9998)
                .count(51)
                .build());

        //영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        //수정된 주문 조회
        OrderItemDtoWithItemFindDto findOrderItemById2 = orderItemService.findOrderItemByIdWithItemFindDto(savedOrderItemId2);
        assertThat(findOrderItemById2.getPrice()).isEqualTo(9998);
        assertThat(findOrderItemById2.getCount()).isEqualTo(51);

        ItemFindDto findItemDtoById2 = itemService.findItemDtoById(itemTestId);
        assertThat(findItemDtoById2.getStock()).isEqualTo(49);
    }

    @Test
    void deleteOrderItem() {
        //가게 열기
        memberService.changeStoreStatus(StoreUpdateDto.builder()
                .id(storeTestId)
                .memberId(memberTestId)
                .build());

        //영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        //when
        //주문 생성
        Long createdOrderItemId = orderItemService.createOrderItem(orderTestId, itemTestId, 10000, 50);

        //영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        //주문 삭제
        orderService.deleteOrderItem(createdOrderItemId);

        //영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        //then
        //주문 삭제 확인
        assertThatThrownBy(() -> orderItemService.findOrderItemDtoById(createdOrderItemId)).isInstanceOf(NoSuchOrderItemException.class);

        //재고 확인
        ItemFindDto findItemDtoById = itemService.findItemDtoById(itemTestId);
        assertThat(findItemDtoById.getStock()).isEqualTo(100);
    }
}