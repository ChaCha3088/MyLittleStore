package site.mylittlestore.controller;

import org.assertj.core.api.Assertions;
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

        StoreDto storeDtoById = storeService.findStoreDtoById(4L);
        assertThat(storeDtoById.getName()).isEqualTo("storeTest");
        assertThat(storeDtoById.getAddress().getCity()).isEqualTo("city");
        assertThat(storeDtoById.getAddress().getStreet()).isEqualTo("street");
        assertThat(storeDtoById.getAddress().getZipcode()).isEqualTo("zipcode");
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
        StoreDto storeDtoById = storeService.findStoreDtoById(3L);
        assertThat(storeDtoById.getName()).isEqualTo("Cha Cha");
        assertThat(storeDtoById.getAddress().getCity()).isEqualTo("newCity");
        assertThat(storeDtoById.getAddress().getStreet()).isEqualTo("newStreet");
        assertThat(storeDtoById.getAddress().getZipcode()).isEqualTo("newZipcode");
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
        StoreDto storeDtoById1 = storeService.findStoreDtoById(5L);
        assertThat(storeDtoById1.getStoreStatus()).isEqualTo(StoreStatus.OPEN);

        //when
        //가게 상태 변경
        mockMvc.perform(get("/members/{memberId}/stores/{storeId}/changeStoreStatus", memberTestId, 5L))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/members/" + memberTestId + "/stores/5"));

        //then
        //가게 상태 변경 확인
        StoreDto storeDtoById2 = storeService.findStoreDtoById(5L);
        assertThat(storeDtoById2.getStoreStatus()).isEqualTo(StoreStatus.CLOSE);
    }
}