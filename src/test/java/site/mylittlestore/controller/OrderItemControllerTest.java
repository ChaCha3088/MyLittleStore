package site.mylittlestore.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import site.mylittlestore.domain.Address;
import site.mylittlestore.dto.item.ItemCreationDto;
import site.mylittlestore.dto.item.ItemFindDto;
import site.mylittlestore.dto.member.MemberCreationDto;
import site.mylittlestore.dto.orderitem.OrderItemFindDto;
import site.mylittlestore.dto.store.StoreDto;
import site.mylittlestore.dto.store.StoreUpdateDto;
import site.mylittlestore.enumstorage.errormessage.OrderItemErrorMessage;
import site.mylittlestore.exception.orderitem.OrderItemException;
import site.mylittlestore.service.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Transactional
public class OrderItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberService memberService;

    @Autowired
    private StoreTableService storeTableService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private ItemService itemService;

    private Long memberTestId;
    private Long storeTestId;
    private Long orderTestId;
    private Long itemTestId1;
    private Long itemTestId2;

    @BeforeAll
    void setUp() {
        //회원 추가
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

        //가게 등록
        Long newStoreId = memberService.createStore(StoreDto.builder()
                .memberId(newMemberId)
                .name("storeTest")
                .address(Address.builder()
                        .city("city")
                        .street("street")
                        .zipcode("zipcode")
                        .build())
                .build());

        //테이블 추가
        Long newOrderId = storeTableService.createStoreTable(storeTestId);

        //상품 추가
        Long newItemId1 = storeService.createItem(ItemCreationDto.builder()
                .storeId(newStoreId)
                .name("itemTest1")
                .price(10000)
                .stock(100)
                .build());

        Long newItemId2 = storeService.createItem(ItemCreationDto.builder()
                .storeId(newStoreId)
                .name("itemTest2")
                .price(5000)
                .stock(50)
                .build());

        //가게 열기
        memberService.changeStoreStatus(StoreUpdateDto.builder()
                .id(newStoreId)
                .memberId(newMemberId)
                .build());

        memberTestId = newMemberId;
        storeTestId = newStoreId;
        orderTestId = newOrderId;
        itemTestId1 = newItemId1;
        itemTestId2 = newItemId2;
    }

    //나중에 주문만 확인할 이유가 생길 때 만들자
//    @Test
//    void orderItemInfo() throws Exception {
//        //when
//        //주문 추가
//        mockMvc.perform(post("/members/{memberId}/stores/{storeId}/storeTables/{storeTableId}/orders/{orderId}/orderItems/new", memberTestId, storeTestId, orderTestId)
//                .param()
//
//                        .storeId(storeTestId)
//                        .orderId(orderTestId)
//                        .itemId(itemTestId1)
//                        .price(10000)
//                        .count(1)
//
//        //then
//        //주문 목록 조회
//        mockMvc.perform(
//
//        //실패
//        assertThat(1).isEqualTo(2);
//    }

    @Test
    void orderItemInfo() throws Exception {
        //given
        //주문 추가
        mockMvc.perform(post("/members/{memberId}/stores/{storeId}/storeTables/{storeTableId}/orders/{orderId}/orderItems/new", memberTestId, storeTestId, orderTestId)
                .param("itemId", itemTestId1.toString())
                .param("price", "10000")
                .param("count", "100"))
                        .andExpect(status().is3xxRedirection());

        //then
        //주문 조회
        mockMvc.perform(get("/members/{memberId}/stores/{storeId}/storeTables/{storeTableId}/orders/{orderId}/orderItems/{orderItemId}", memberTestId, storeTestId, orderTestId, 7L))
                .andExpect(status().isOk())
                .andExpect(view().name("orderItems/orderItemInfo"));
    }

    @Test
    void createOrderItemForm() throws Exception {
        //then
        //주문 추가 폼 조회
        mockMvc.perform(get("/members/{memberId}/stores/{storeId}/storeTables/{storeTableId}/orders/{orderId}/orderItems/new", memberTestId, storeTestId, orderTestId))
                .andExpect(status().isOk())
                .andExpect(view().name("orderItems/orderItemCreationForm"));
    }

    @Test
    void createOrderItem() throws Exception {
        //given
        //주문 추가
        mockMvc.perform(post("/members/{memberId}/stores/{storeId}/storeTables/{storeTableId}/orders/{orderId}/orderItems/new", memberTestId, storeTestId, orderTestId)
                .param("itemId", itemTestId1.toString())
                .param("price", "10000")
                .param("count", "100"));

        //when
        //주문 조회
        mockMvc.perform(get("/members/{memberId}/stores/{storeId}/storeTables/{storeTableId}/orders/{orderId}/orderItems/{orderItemId}", memberTestId, storeTestId, orderTestId, 6))
                .andExpect(status().isOk())
                .andExpect(view().name("orderItems/orderItemInfo"));

        //then
        OrderItemFindDto findOrderItemFindDtoById = orderItemService.findOrderItemDtoById(6L);
        assertThat(findOrderItemFindDtoById.getPrice()).isEqualTo(10000);
        assertThat(findOrderItemFindDtoById.getCount()).isEqualTo(100);
        assertThat(findOrderItemFindDtoById.getItemId()).isEqualTo(itemTestId1);

        ItemFindDto findItemDtoById = itemService.findItemDtoById(itemTestId1);
        assertThat(findItemDtoById.getStock()).isEqualTo(0);
    }

    @Test
    void updateOrderItem() throws Exception {
        //given
        //주문 추가
        mockMvc.perform(post("/members/{memberId}/stores/{storeId}/storeTables/{storeTableId}/orders/{orderId}/orderItems/new", memberTestId, storeTestId, orderTestId)
                .param("itemId", itemTestId1.toString())
                .param("price", "10000")
                .param("count", "100"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/members/"+memberTestId+"/stores/"+storeTestId+"/orders/"+orderTestId+"/orderItems/8"));

        //when
        //주문 수정(수량 줄이기)
        mockMvc.perform(post("/members/{memberId}/stores/{storeId}/storeTables/{storeTableId}/orders/{orderId}/orderItems/{orderItemId}/update", memberTestId, storeTestId, orderTestId, 8)
                .param("price", "5000")
                .param("count", "50"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/members/"+memberTestId+"/stores/"+storeTestId+"/orders/"+orderTestId+"/orderItems/8"));

        //then
        OrderItemFindDto findOrderItemDtoById1 = orderItemService.findOrderItemDtoById(8L);
        assertThat(findOrderItemDtoById1.getPrice()).isEqualTo(5000);
        assertThat(findOrderItemDtoById1.getCount()).isEqualTo(50);
        assertThat(findOrderItemDtoById1.getItemId()).isEqualTo(itemTestId1);

        ItemFindDto findItemDtoById1 = itemService.findItemDtoById(itemTestId1);
        assertThat(findItemDtoById1.getStock()).isEqualTo(50);

        //when
        //주문 수정(수량 늘리기)
        mockMvc.perform(post("/members/{memberId}/stores/{storeId}/storeTables/{storeTableId}/orders/{orderId}/orderItems/{orderItemId}/update", memberTestId, storeTestId, orderTestId, 8)
                .param("price", "7500")
                .param("count", "75"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/members/"+memberTestId+"/stores/"+storeTestId+"/orders/"+orderTestId+"/orderItems/8"));

        //then
        OrderItemFindDto findOrderItemDtoById2 = orderItemService.findOrderItemDtoById(8L);
        assertThat(findOrderItemDtoById2.getPrice()).isEqualTo(7500);
        assertThat(findOrderItemDtoById2.getCount()).isEqualTo(75);

        ItemFindDto findItemDtoById2 = itemService.findItemDtoById(itemTestId1);
        assertThat(findItemDtoById2.getStock()).isEqualTo(25);
    }

    @Test
    void deleteOrderItem() throws Exception {
        //given
        //주문 추가
        String redirectedUrl1 = mockMvc.perform(post("/members/{memberId}/stores/{storeId}/storeTables/{storeTableId}/orders/{orderId}/orderItems/new", memberTestId, storeTestId, orderTestId)
                        .param("itemId", itemTestId1.toString())
                        .param("price", "10000")
                        .param("count", "50"))
                .andExpect(status().is3xxRedirection())
                .andReturn().getResponse().getRedirectedUrl();

        String[] split = redirectedUrl1.split("/");
        Long orderItemId = Long.parseLong(split[split.length - 1]);

        ItemFindDto findItemDtoById1 = itemService.findItemDtoById(itemTestId1);
        assertThat(findItemDtoById1.getStock()).isEqualTo(50);

        //when
        mockMvc.perform(get("/members/{memberId}/stores/{storeId}/storeTables/{storeTableId}/orders/{orderId}/orderItems/{orderItemId}/delete", memberTestId, storeTestId, orderTestId, orderItemId))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/members/" + memberTestId + "/stores/" + storeTestId + "/orders/" + orderTestId));

        //then
        assertThatThrownBy(() -> orderItemService.findOrderItemDtoById(orderItemId))
                .isInstanceOf(OrderItemException.class)
                .hasMessageContaining(OrderItemErrorMessage.NO_SUCH_ORDER_ITEM.getMessage());

        ItemFindDto findItemDtoById2 = itemService.findItemDtoById(itemTestId1);
        assertThat(findItemDtoById2.getStock()).isEqualTo(100);
    }
}
