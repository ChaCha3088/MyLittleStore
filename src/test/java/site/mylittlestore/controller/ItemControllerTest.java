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
import site.mylittlestore.domain.item.Item;
import site.mylittlestore.dto.item.ItemFindDto;
import site.mylittlestore.dto.member.MemberCreationDto;
import site.mylittlestore.dto.store.StoreDto;
import site.mylittlestore.dto.store.StoreUpdateDto;
import site.mylittlestore.dto.order.OrderDto;
import site.mylittlestore.enumstorage.errormessage.ItemErrorMessage;
import site.mylittlestore.enumstorage.status.ItemStatus;
import site.mylittlestore.exception.item.NoSuchItemException;
import site.mylittlestore.repository.item.ItemRepository;
import site.mylittlestore.service.ItemService;
import site.mylittlestore.service.MemberService;
import site.mylittlestore.service.StoreService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Transactional
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberService memberService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    private Long memberTestId;
    private Long storeTestId;
    private Long orderTestId;

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
        Long newOrderId = storeService.createStoreTable(OrderDto.builder()
                .storeId(newStoreId)
                .build());

        //가게 열기
        memberService.changeStoreStatus(StoreUpdateDto.builder()
                .id(newStoreId)
                .memberId(newMemberId)
                .build());

        memberTestId = newMemberId;
        storeTestId = newStoreId;
        orderTestId = newOrderId;
    }

    @Test
    void itemList() {
        //when
        //mockMvc로 상품 추가


        //then
        //mockMvc로 상품 목록 조회 및 검증

        //실패
        assertThat(1).isEqualTo(2);
    }

    @Test
    void itemInfo() {
        //when
        //mockMvc로 상품 추가

        //then
        //mockMvc로 상품 목록 조회 및 검증

        //실패
        assertThat(1).isEqualTo(2);
    }

    @Test
    void createItemForm() {
        //given


        //when


        //then

        //실패
        assertThat(1).isEqualTo(2);
    }

    @Test
    void createItem() {
        //given


        //when


        //then

        //실패
        assertThat(1).isEqualTo(2);
    }

    @Test
    void updateItemForm() throws Exception {
        //given
        //상품 추가
        mockMvc.perform(post("/members/{memberId}/stores/{storeId}/items/new", memberTestId, storeTestId)
                .param("name", "itemTest")
                .param("price", "1000")
                .param("stock", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/members/"+memberTestId+"/stores/"+storeTestId+"/items/"+4));

        //then
        //상품 수정 폼 요청
        mockMvc.perform(get("/members/{memberId}/stores/{storeId}/items/{itemId}/update", memberTestId, storeTestId, 4))
                .andExpect(status().isOk())
                .andExpect(view().name("items/itemUpdateForm"))
                .andExpect(model().attributeExists("itemUpdateForm"));
    }

    @Test
    void updateItem() throws Exception {
        //given
        //상품 추가
        mockMvc.perform(post("/members/{memberId}/stores/{storeId}/items/new", memberTestId, storeTestId)
                        .param("name", "itemTest")
                        .param("price", "1000")
                        .param("stock", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/members/"+memberTestId+"/stores/"+storeTestId+"/items/"+4));

        //when
        mockMvc.perform(put("/members/{memberId}/stores/{storeId}/items/{itemId}/update", memberTestId, storeTestId, 4)
                        .param("name", "newItemTest")
                        .param("price", "9999")
                        .param("stock", "9"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/members/"+memberTestId+"/stores/"+storeTestId+"/items/"+4));

        //then
        ItemFindDto findItemDtoById = itemService.findItemDtoById(4L);

        assertThat(findItemDtoById.getName()).isEqualTo("newItemTest");
        assertThat(findItemDtoById.getPrice()).isEqualTo(9999);
        assertThat(findItemDtoById.getStock()).isEqualTo(9);
    }

    @Test
    void deleteItem() throws Exception {
        //given
        //상품 추가
        mockMvc.perform(post("/members/{memberId}/stores/{storeId}/items/new", memberTestId, storeTestId)
                        .param("name", "itemTest")
                        .param("price", "1000")
                        .param("stock", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/members/"+memberTestId+"/stores/"+storeTestId+"/items/"+5));

        //when
        mockMvc.perform(put("/members/{memberId}/stores/{storeId}/items/{itemId}/delete", memberTestId, storeTestId, 5))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/members/"+memberTestId+"/stores/"+storeTestId+"/items"));

        //then
        assertThatThrownBy(() -> itemService.findItemDtoById(5L))
                .isInstanceOf(NoSuchItemException.class)
                .hasMessage(ItemErrorMessage.NO_SUCH_ITEM.getMessage());
        Optional<Item> findById = itemRepository.findById(5L);
        assertThat(findById.get().getItemStatus()).isEqualTo(ItemStatus.DELETED);
    }
}
