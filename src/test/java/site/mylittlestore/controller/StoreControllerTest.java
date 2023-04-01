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
import site.mylittlestore.dto.store.StoreDtoWithStoreTableFindDtosAndItemFindDtos;
import site.mylittlestore.enumstorage.status.StoreStatus;
import site.mylittlestore.service.MemberService;
import site.mylittlestore.service.StoreService;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Transactional
class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StoreService storeService;

    @Autowired
    private MemberService memberService;

    @Autowired
    EntityManager em;

    private Long memberTestId;



    @BeforeAll
    void setUp() {
        Long savedMemberId = memberService.joinMember(MemberCreationDto.builder()
                .name("memberTest")
                .email("memberTest@gmail.com")
                .password("password")
                .address(Address.builder()
                        .city("city")
                        .street("street")
                        .zipcode("zipcode")
                        .build())
                .build());

        memberTestId = savedMemberId;
    }

    @Test
    void storeInfo() throws Exception {
        //given
        mockMvc.perform(post("/members/{memberId}/stores/new", memberTestId)
                        .param("name", "storeTest")
                        .param("city", "city")
                        .param("street", "street")
                        .param("zipcode", "zipcode"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/members/" + memberTestId + "/stores/6"));

        //then
        mockMvc.perform(get("/members/{memberId}/stores/{storeId}", memberTestId, 6L))
                .andExpect(status().isOk())
                .andExpect(view().name("stores/storeInfo"))
                .andExpect(model().attributeExists("memberId"))
                .andExpect(model().attributeExists("storeDto"));
    }

    @Test
    void createStoreForm() throws Exception {
        mockMvc.perform(get("/members/{memberId}/stores/new", memberTestId))
                .andExpect(status().isOk())
                .andExpect(view().name("stores/storeCreationForm"))
                .andExpect(model().attributeExists("storeCreationForm"));
    }

    @Test
    void createStore() throws Exception {
        mockMvc.perform(post("/members/{memberId}/stores/new", memberTestId)
                        .param("name", "storeTest")
                        .param("city", "city")
                        .param("street", "street")
                        .param("zipcode", "zipcode"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/members/" + memberTestId + "/stores/4"));

        StoreDtoWithStoreTableFindDtosAndItemFindDtos storeDtoWithStoreTableFindDtosAndItemFindDtosById = storeService.findStoreDtoWithStoreTableFindDtosAndItemFindDtosById(4L);
        assertThat(storeDtoWithStoreTableFindDtosAndItemFindDtosById.getName()).isEqualTo("storeTest");
        assertThat(storeDtoWithStoreTableFindDtosAndItemFindDtosById.getAddressDto().getCity()).isEqualTo("city");
        assertThat(storeDtoWithStoreTableFindDtosAndItemFindDtosById.getAddressDto().getStreet()).isEqualTo("street");
        assertThat(storeDtoWithStoreTableFindDtosAndItemFindDtosById.getAddressDto().getZipcode()).isEqualTo("zipcode");
    }

    @Test
    void createStoreError() throws Exception {
        mockMvc.perform(post("/members/{memberId}/stores/new", memberTestId)
                        .param("name", "storeTest")
//                        .param("city", "city")
                        .param("street", "street")
                        .param("zipcode", "zipcode"))
                .andExpect(view().name("stores/storeCreationForm"))
                .andExpect(model().attributeExists("storeCreationForm"));
    }

    @Test
    void updateStoreForm() throws Exception {
        //given
        //가게 생성
        mockMvc.perform(post("/members/{memberId}/stores/new", memberTestId)
                        .param("name", "storeTest")
                        .param("city", "city")
                        .param("street", "street")
                        .param("zipcode", "zipcode"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/members/" + memberTestId + "/stores/2"));

        //then
        //가게 수정 폼
        mockMvc.perform(get("/members/{memberId}/stores/{storeId}/update", memberTestId, 2L))
                .andExpect(status().isOk())
                .andExpect(view().name("stores/storeUpdateForm"))
                .andExpect(model().attributeExists("storeUpdateForm"));
    }

    @Test
    void updateStore() throws Exception {
        //given
        //가게 생성
        mockMvc.perform(post("/members/{memberId}/stores/new", memberTestId)
                        .param("name", "storeTest")
                        .param("city", "city")
                        .param("street", "street")
                        .param("zipcode", "zipcode"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/members/" + memberTestId + "/stores/3"));

        //when
        //가게 수정
        mockMvc.perform(post("/members/{memberId}/stores/{storeId}/update", memberTestId, 3L)
                        .param("name", "Cha Cha")
                        .param("city", "newCity")
                        .param("street", "newStreet")
                        .param("zipcode", "newZipcode"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/members/" + memberTestId + "/stores/3"));

        //then
        //가게 수정 확인
        StoreDtoWithStoreTableFindDtosAndItemFindDtos storeDtoWithStoreTableFindDtosAndItemFindDtosById = storeService.findStoreDtoWithStoreTableFindDtosAndItemFindDtosById(3L);
        assertThat(storeDtoWithStoreTableFindDtosAndItemFindDtosById.getName()).isEqualTo("Cha Cha");
        assertThat(storeDtoWithStoreTableFindDtosAndItemFindDtosById.getAddressDto().getCity()).isEqualTo("newCity");
        assertThat(storeDtoWithStoreTableFindDtosAndItemFindDtosById.getAddressDto().getStreet()).isEqualTo("newStreet");
        assertThat(storeDtoWithStoreTableFindDtosAndItemFindDtosById.getAddressDto().getZipcode()).isEqualTo("newZipcode");
    }

    @Test
    void changeStoreStatus() throws Exception {
        //given
        //가게 생성
        mockMvc.perform(post("/members/{memberId}/stores/new", memberTestId)
                        .param("name", "storeTest")
                        .param("city", "city")
                        .param("street", "street")
                        .param("zipcode", "zipcode"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/members/" + memberTestId + "/stores/5"));

        //when
        //가게 상태 변경
        mockMvc.perform(get("/members/{memberId}/stores/{storeId}/changeStoreStatus", memberTestId, 5L))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/members/" + memberTestId + "/stores/5"));

        //then
        //가게 상태 변경 확인
        StoreDtoWithStoreTableFindDtosAndItemFindDtos storeDtoWithStoreTableFindDtosAndItemFindDtosById1 = storeService.findStoreDtoWithStoreTableFindDtosAndItemFindDtosById(5L);
        assertThat(storeDtoWithStoreTableFindDtosAndItemFindDtosById1.getStoreStatus()).isEqualTo(StoreStatus.OPEN);

        //when
        //가게 상태 변경
        mockMvc.perform(get("/members/{memberId}/stores/{storeId}/changeStoreStatus", memberTestId, 5L))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/members/" + memberTestId + "/stores/5"));

        //then
        //가게 상태 변경 확인
        StoreDtoWithStoreTableFindDtosAndItemFindDtos storeDtoWithStoreTableFindDtosAndItemFindDtosById2 = storeService.findStoreDtoWithStoreTableFindDtosAndItemFindDtosById(5L);
        assertThat(storeDtoWithStoreTableFindDtosAndItemFindDtosById2.getStoreStatus()).isEqualTo(StoreStatus.CLOSE);
    }
}