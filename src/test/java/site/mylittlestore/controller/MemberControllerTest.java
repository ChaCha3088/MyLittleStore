package site.mylittlestore.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import site.mylittlestore.domain.Address;
import site.mylittlestore.dto.member.MemberCreationDto;
import site.mylittlestore.dto.member.MemberFindDto;
import site.mylittlestore.dto.store.StoreDto;
import site.mylittlestore.service.MemberService;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MemberControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberService memberService;

    @Test
    void memberList() throws Exception {
        //given
        mockMvc.perform(post("/members/new")
                        .param("name", "memberTest1")
                        .param("email", "memberTest1@email.com")
                        .param("password", "password")
                        .param("city", "city")
                        .param("street", "street")
                        .param("zipcode", "zipcode"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/members/1"));

        mockMvc.perform(post("/members/new")
                        .param("name", "memberTest2")
                        .param("email", "memberTest2@email.com")
                        .param("password", "password")
                        .param("city", "city")
                        .param("street", "street")
                        .param("zipcode", "zipcode"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/members/2"));

        //then
        mockMvc.perform(get("/members"))
                .andExpect(status().isOk())
                .andExpect(view().name("members/memberList"))
                .andExpect(model().attributeExists("memberFindDtoList"));

        List<MemberFindDto> findAllMemberFindDto = memberService.findAllMemberFindDto();
        assertThat(findAllMemberFindDto.size()).isEqualTo(2);
    }

    @Test
    void memberInfo() throws Exception {
        //given
        Long savedMemberId = memberService.joinMember(MemberCreationDto.builder()
                .name("memberTest")
                .email("memberTest@email.com")
                .password("password")
                .address(Address.builder()
                        .city("city")
                        .street("street")
                        .zipcode("zipcode")
                        .build())
                .build());

        Long savedStoreId = memberService.createStore(StoreDto.builder()
                .memberId(savedMemberId)
                .name("storeTest")
                .address(Address.builder()
                        .city("city")
                        .street("street")
                        .zipcode("zipcode")
                        .build())
                .build());

        //then
        mockMvc.perform(get("/members/{memberId}", savedMemberId))
                .andExpect(status().isOk())
                .andExpect(view().name("members/memberInfo"))
                .andExpect(model().attributeExists("memberFindDto"))
                .andExpect(model().attributeExists("storeDtoList"));
    }

    @Test
    void createMemberForm() throws Exception {
        mockMvc.perform(get("/members/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("members/memberCreationForm"))
                .andExpect(model().attributeExists("memberCreationForm"));
    }

    @Test
    void createMember() throws Exception {
        mockMvc.perform(post("/members/new")
                        .param("name", "memberTest")
                        .param("email", "memberTest@email.com")
                        .param("password", "password")
                        .param("city", "city")
                        .param("street", "street")
                        .param("zipcode", "zipcode"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/members/1"));

        MemberFindDto findMemberByMemberEmail = memberService.findMemberByMemberEmail("memberTest@email.com");
        assertThat(findMemberByMemberEmail.getName()).isEqualTo("memberTest");
        assertThat(findMemberByMemberEmail.getEmail()).isEqualTo("memberTest@email.com");
        assertThat(findMemberByMemberEmail.getAddress().getCity()).isEqualTo("city");
        assertThat(findMemberByMemberEmail.getAddress().getStreet()).isEqualTo("street");
        assertThat(findMemberByMemberEmail.getAddress().getZipcode()).isEqualTo("zipcode");
    }

    @Test
    void createMemberError() throws Exception {
        //then
        mockMvc.perform(post("/members/new")
                        .param("name", "memberTest")
                        .param("email", "memberTest")
                        .param("password", "password")
                        .param("city", "city")
                        .param("street", "street")
                        .param("zipcode", "zipcode"))
                .andExpect(view().name("members/memberCreationForm"));
    }

    @Test
    void updateMemberForm() throws Exception {
        //given
        mockMvc.perform(post("/members/new")
                        .param("name", "memberTestName")
                        .param("email", "memberTest@email.com")
                        .param("password", "password")
                        .param("city", "city")
                        .param("street", "street")
                        .param("zipcode", "zipcode"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/members/1"));

        //then
        mockMvc.perform(get("/members/{memberId}/update", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("members/memberUpdateForm"))
                .andExpect(model().attributeExists("memberUpdateForm"));
    }

    @Test
    void updateMember() throws Exception {
        //given
        mockMvc.perform(post("/members/new")
                .param("name", "memberTestName")
                .param("email", "memberTest@email.com")
                .param("password", "password")
                .param("city", "city")
                .param("street", "street")
                .param("zipcode", "zipcode"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/members/1"));

        mockMvc.perform(post("/members/1/update")
                .param("name", "updateMemberTestName")
                .param("city", "updateCity")
                .param("street", "updateStreet")
                .param("zipcode", "updateZipcode"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/members/1"));

        MemberFindDto findMemberByMemberEmail = memberService.findMemberByMemberEmail("memberTest@email.com");
        assertThat(findMemberByMemberEmail.getName()).isEqualTo("updateMemberTestName");
        assertThat(findMemberByMemberEmail.getAddress().getCity()).isEqualTo("updateCity");
        assertThat(findMemberByMemberEmail.getAddress().getStreet()).isEqualTo("updateStreet");
        assertThat(findMemberByMemberEmail.getAddress().getZipcode()).isEqualTo("updateZipcode");
    }

    @Test
    void updateMemberError() throws Exception {
        //given
        mockMvc.perform(post("/members/new")
                        .param("name", "memberTestName")
                        .param("email", "memberTest@email.com")
                        .param("password", "password")
                        .param("city", "city")
                        .param("street", "street")
                        .param("zipcode", "zipcode"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/members/1"));

        //then
        mockMvc.perform(post("/members/{memberId}/update", 1L)
                        .param("name", "")
                        .param("city", "city")
                        .param("street", "street")
                        .param("zipcode", "zipcode"))
                .andExpect(view().name("members/memberUpdateForm"));
    }
}