package site.mylittlestore.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import site.mylittlestore.domain.Address;
import site.mylittlestore.dto.item.ItemCreationDto;
import site.mylittlestore.dto.item.ItemFindDto;
import site.mylittlestore.dto.member.MemberCreationDto;
import site.mylittlestore.dto.orderitem.OrderItemDtoWithItemFindDto;
import site.mylittlestore.dto.orderitem.OrderItemFindDto;
import site.mylittlestore.dto.store.StoreDto;
import site.mylittlestore.dto.store.StoreUpdateDto;
import site.mylittlestore.exception.item.NotEnoughStockException;
import site.mylittlestore.exception.store.StoreClosedException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
//@Transactional
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
    void createOrderItem() {
        //given
        //상품 추가
        Long newItemId = storeService.createItem(ItemCreationDto.builder()
                .storeId(storeTestId)
                .name("itemTest2")
                .price(9999)
                .stock(99)
                .build());

        //주문 상품 생성
        Long createdOrderItemId1 = orderItemService.createOrderItem(orderTestId, itemTestId, 10000, 1);

        Long createdOrderItemId2 = orderItemService.createOrderItem(orderTestId, newItemId, 9999, 10);

        //when
        //생성된 주문 상품 조회
        OrderItemDtoWithItemFindDto findOrderItemById1 = orderItemService.findOrderItemByIdWithItemFindDto(createdOrderItemId1);
        OrderItemDtoWithItemFindDto findOrderItemById2 = orderItemService.findOrderItemByIdWithItemFindDto(createdOrderItemId2);

        //then
        assertThat(findOrderItemById1.getItemFindDto().getId()).isEqualTo(itemTestId);
        assertThat(findOrderItemById1.getPrice()).isEqualTo(10000);
        assertThat(findOrderItemById1.getCount()).isEqualTo(1);

        assertThat(findOrderItemById2.getItemFindDto().getId()).isEqualTo(newItemId);
        assertThat(findOrderItemById2.getPrice()).isEqualTo(9999);
        assertThat(findOrderItemById2.getCount()).isEqualTo(10);

        //주문 상품 개수 확인
        List<OrderItemFindDto> findAllOrderItemByOrderId = orderService.findAllOrderItemByOrderId(orderTestId);
        assertThat(findAllOrderItemByOrderId.size()).isEqualTo(2);

        //재고 관련 확인
        ItemFindDto itemfindDto = itemService.findItemDtoById(itemTestId);
        assertThat(itemfindDto.getStock()).isEqualTo(99);

        ItemFindDto newItemFindDto = itemService.findItemDtoById(newItemId);
        assertThat(newItemFindDto.getStock()).isEqualTo(89);
    }

    @Test
    @DisplayName("주문 상품 생성 시 같은 상품, 같은 가격일 경우")
    void createOrderItemWithSameItemIdAndSameItemPrice() {
        //given
        //주문 상품 생성
        Long createdOrderItemId1 = orderItemService.createOrderItem(orderTestId, itemTestId, 10000, 99);

        //같은 상품으로 주문 상품 생성
        Long createdOrderItemId2 = orderItemService.createOrderItem(orderTestId, itemTestId, 10000, 1);

        //when
        //생성된 주문 상품 조회
        OrderItemDtoWithItemFindDto findOrderItemById1 = orderItemService.findOrderItemByIdWithItemFindDto(createdOrderItemId1);

        //then
        assertThat(findOrderItemById1.getItemFindDto().getId()).isEqualTo(itemTestId);
        assertThat(findOrderItemById1.getPrice()).isEqualTo(10000);
        assertThat(findOrderItemById1.getCount()).isEqualTo(100);

        //주문 상품 개수 확인
        List<OrderItemFindDto> findAllOrderItemByOrderId = orderService.findAllOrderItemByOrderId(orderTestId);
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
        Long createdOrderItemId1 = orderItemService.createOrderItem(orderTestId, itemTestId, 10000, 1);

        //같은 상품으로 주문 상품 생성
        Long createdOrderItemId2 = orderItemService.createOrderItem(orderTestId, itemTestId, 8000, 1);

        //when
        //생성된 주문 상품 조회
        OrderItemDtoWithItemFindDto findOrderItemById1 = orderItemService.findOrderItemByIdWithItemFindDto(createdOrderItemId1);
        OrderItemDtoWithItemFindDto findOrderItemById2 = orderItemService.findOrderItemByIdWithItemFindDto(createdOrderItemId2);

        //then
        assertThat(findOrderItemById1.getItemFindDto().getId()).isEqualTo(itemTestId);
        assertThat(findOrderItemById1.getPrice()).isEqualTo(10000);
        assertThat(findOrderItemById1.getCount()).isEqualTo(1);

        assertThat(findOrderItemById2.getItemFindDto().getId()).isEqualTo(itemTestId);
        assertThat(findOrderItemById2.getPrice()).isEqualTo(8000);
        assertThat(findOrderItemById2.getCount()).isEqualTo(1);

        //주문 상품 개수 확인
        List<OrderItemFindDto> findAllOrderItemByOrderId = orderService.findAllOrderItemByOrderId(orderTestId);
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
        Long createdOrderItemId1 = orderItemService.createOrderItem(orderTestId, itemTestId, 10000, 50);

        Long createdOrderItemId2 = orderItemService.createOrderItem(orderTestId, itemTestId, 8000, 50);

        //when
        //생성된 주문 상품 조회
        OrderItemDtoWithItemFindDto findOrderItemById1 = orderItemService.findOrderItemByIdWithItemFindDto(createdOrderItemId1);
        OrderItemDtoWithItemFindDto findOrderItemById2 = orderItemService.findOrderItemByIdWithItemFindDto(createdOrderItemId2);

        //then
        assertThat(findOrderItemById1.getItemFindDto().getId()).isEqualTo(itemTestId);
        assertThat(findOrderItemById1.getPrice()).isEqualTo(10000);
        assertThat(findOrderItemById1.getCount()).isEqualTo(50);

        assertThat(findOrderItemById2.getItemFindDto().getId()).isEqualTo(itemTestId);
        assertThat(findOrderItemById2.getPrice()).isEqualTo(8000);
        assertThat(findOrderItemById2.getCount()).isEqualTo(50);

        //주문 상품 개수 확인
        List<OrderItemFindDto> findAllOrderItemByOrderId = orderService.findAllOrderItemByOrderId(orderTestId);
        assertThat(findAllOrderItemByOrderId.size()).isEqualTo(2);
        assertThat(createdOrderItemId1).isNotEqualTo(createdOrderItemId2);

        //재고 관련 확인
        ItemFindDto itemFindDto = itemService.findItemDtoById(itemTestId);
        assertThat(itemFindDto.getStock()).isEqualTo(0);

        //then
        //재고 부족 예외 발생
        assertThatThrownBy(() -> orderItemService.createOrderItem(orderTestId, itemTestId, 8000, 1)).isInstanceOf(NotEnoughStockException.class);
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
        assertThatThrownBy(() -> orderItemService.createOrderItem(orderTestId, itemTestId, 10000, 50))
                .isInstanceOf(StoreClosedException.class);
    }
}
