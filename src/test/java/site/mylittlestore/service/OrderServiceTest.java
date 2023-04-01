package site.mylittlestore.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import site.mylittlestore.domain.Address;
import site.mylittlestore.dto.item.ItemCreationDto;
import site.mylittlestore.dto.order.OrderDto;
import site.mylittlestore.dto.member.MemberCreationDto;
import site.mylittlestore.dto.store.StoreDtoWithStoreTableFindDtosAndItemFindDtos;
import site.mylittlestore.dto.store.StoreUpdateDto;
import site.mylittlestore.dto.storetable.StoreTableFindDtoWithOrderFindDto;
import site.mylittlestore.enumstorage.status.OrderStatus;
import site.mylittlestore.exception.store.NoSuchOrderException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Sql(scripts = {"classpath:sql/test.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
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
        OrderDto findOrderWithOrderItemIdById = orderService.findOrderDtoById(orderTestId);

        //then
        assertThat(findOrderWithOrderItemIdById.getOrderStatus()).isEqualTo(OrderStatus.USING);
    }

    @Test
    void findOrderByIdNoSuchOrderException() {
        //then
        assertThatThrownBy(() -> orderService.findOrderDtoById(100L))
                .isInstanceOf(NoSuchOrderException.class);
    }

//    @Test
//    void findOrderDtoById() {
//        //given
//        //가게 열기
//        memberService.changeStoreStatus(StoreUpdateDto.builder()
//                .id(storeTestId)
//                .memberId(memberTestId)
//                .build());
//
//        orderItemService.createOrderItem(OrderItemCreationDto.builder()
//                .orderId(orderTestId)
//                .itemId(itemTestId)
//                .price(10000)
//                .count(1)
//                .build());
//
//        //영속성 컨텍스트 초기화
//        em.flush();
//        em.clear();
//
//        //when
//        OrderDtoWithOrderItemDtoWithItemFindDto orderDtoWithOrderItemDtoWithItemFindDtoById = orderService.findOrderDtoWithOrderItemDtoWithItemFindDtoById(orderTestId);
//
//        //then
//        assertThat(orderDtoWithOrderItemDtoWithItemFindDtoById.getOrderStatus()).isEqualTo(OrderStatus.USING);
//        assertThat(orderDtoWithOrderItemDtoWithItemFindDtoById.getOrderItemDtoWithItemFindDtos().size()).isEqualTo(1);
//        assertThat(orderDtoWithOrderItemDtoWithItemFindDtoById.getOrderItemDtoWithItemFindDtos().get(0).getPrice()).isEqualTo(10000);
//        assertThat(orderDtoWithOrderItemDtoWithItemFindDtoById.getOrderItemDtoWithItemFindDtos().get(0).getItemFindDto().getName()).isEqualTo("itemTest");
//    }
    
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
        assertThat(storeTableFindDtoWithOrderFindDtoByStoreId.getOrderDto().getOrderStatus()).isEqualTo(OrderStatus.USING.toString());
    }

    @Test
    @DisplayName("결제가 없을 때, 결제 시작")
    void startPaymentWhenNoPayment() {
        //given


        //when


        //then

        assertThat(1).isEqualTo(2);
    }

    @Test
    @DisplayName("결제가 이미 있을 때, 결제 시작")
    void startPaymentWhenPayment() {
        //given


        //when


        //then

        assertThat(1).isEqualTo(2);
    }
}