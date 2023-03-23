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
import site.mylittlestore.dto.member.MemberCreationDto;
import site.mylittlestore.dto.store.StoreDto;
import site.mylittlestore.dto.order.OrderDtoWithOrderItemDto;
import site.mylittlestore.service.MemberService;
import site.mylittlestore.service.OrderService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberService memberService;

    @Autowired
    private OrderService orderService;

    private Long memberTestId;

    private Long storeTestId;

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

        memberTestId = newMemberId;
        storeTestId = newStoreId;
    }

    @Test
    void orderList() throws Exception {
        //given
        //order 생성
        mockMvc.perform(get("/members/{memberId}/stores/{storeId}/orders/new", memberTestId, storeTestId));
        mockMvc.perform(get("/members/{memberId}/stores/{storeId}/orders/new", memberTestId, storeTestId));

        //when
        mockMvc.perform(get("/members/{memberId}/stores/{storeId}/orders", memberTestId, storeTestId))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/orderList"))
                .andExpect(model().attributeExists("memberId"))
                .andExpect(model().attributeExists("storeId"))
                .andExpect(model().attributeExists("OrderDtoWithOrderItemDtoList"));

        //then
        List<OrderDtoWithOrderItemDto> findAllOrderDtoWithOrderItemIdByStoreId = orderService.findAllOrderDtoWithOrderItemIdByStoreId(storeTestId);
        assertThat(findAllOrderDtoWithOrderItemIdByStoreId.size()).isEqualTo(2);
    }

    @Test
    void orderInfo() throws Exception {
        //given
        //order 생성
        mockMvc.perform(get("/members/{memberId}/stores/{storeId}/orders/new", memberTestId, storeTestId));

        //then
        mockMvc.perform(get("/members/{memberId}/stores/{storeId}/orders/{orderId}", memberTestId, storeTestId, 3L))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/orderInfo"))
                .andExpect(model().attributeExists("memberId"))
                .andExpect(model().attributeExists("OrderDtoWithOrderItemDto"));
    }

    @Test
    void createOrder() throws Exception {
        mockMvc.perform(get("/members/{memberId}/stores/{storeId}/orders/new", memberTestId, storeTestId))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/members/" + memberTestId + "/stores/"+ storeTestId + "/orders/6"));

        OrderDtoWithOrderItemDto findOrderDtoById = orderService.findOrderDtoById(6L);
        assertThat(findOrderDtoById.getStoreId()).isEqualTo(storeTestId);
        assertThat(findOrderDtoById.getOrderNumber()).isEqualTo(1);
    }

}