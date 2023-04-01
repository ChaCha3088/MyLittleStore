package site.mylittlestore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import site.mylittlestore.domain.Address;
import site.mylittlestore.dto.member.MemberCreationDto;
import site.mylittlestore.dto.member.MemberFindDto;
import site.mylittlestore.dto.member.MemberUpdateDto;
import site.mylittlestore.dto.store.StoreDto;
import site.mylittlestore.form.MemberCreationForm;
import site.mylittlestore.form.MemberUpdateForm;
import site.mylittlestore.service.MemberService;
import site.mylittlestore.service.StoreService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final StoreService storeService;

    @GetMapping("/members")
    public String memberList(Model model) {
        List<MemberFindDto> findAllMemberFindDto = memberService.findAllMemberFindDto();

        model.addAttribute("memberFindDtos", findAllMemberFindDto);

        return "members/memberList";
    }

    @GetMapping("/members/{memberId}")
    public String memberInfo(@PathVariable("memberId") Long memberId, Model model) {
        MemberFindDto memberFindDto = memberService.findMemberFindDtoByMemberId(memberId);
        List<StoreDto> findAllStoreDtoByMemberId = storeService.findAllStoreDtoByMemberId(memberId);
        model.addAttribute("memberFindDto", memberFindDto);
        model.addAttribute("storeDtos", findAllStoreDtoByMemberId);
        return "members/memberInfo";
    }

    @GetMapping("/members/new")
    public String createMemberForm(Model model) {
        model.addAttribute("memberCreationForm", new MemberCreationForm());

        return "members/memberCreationForm";
    }

    @PostMapping("/members/new")
    public String createMember(@RequestBody @Valid MemberCreationForm memberCreationForm, BindingResult result) {

        if (result.hasErrors()) {
            return "members/memberCreationForm";
        }

        Long savedMemberId = memberService.joinMember(MemberCreationDto.builder()
                .name(memberCreationForm.getName())
                .email(memberCreationForm.getEmail())
                .password(memberCreationForm.getPassword())
                .address(Address.builder().
                        city(memberCreationForm.getCity())
                        .street(memberCreationForm.getStreet())
                        .zipcode(memberCreationForm.getZipcode())
                        .build())
                .build());

        return "redirect:/members/"+savedMemberId;
    }

    @GetMapping("/members/{memberId}/update")
    public String updateMemberForm(@PathVariable("memberId") Long memberId, Model model) {
        model.addAttribute("memberFindDto", memberService.findMemberFindDtoByMemberId(memberId));
        model.addAttribute("memberUpdateForm", new MemberUpdateForm());

        return "members/memberUpdateForm";
    }

    @PostMapping("/members/{memberId}/update")
    public String updateMember(@PathVariable("memberId") Long memberId, @RequestBody @Valid MemberUpdateForm memberUpdateForm, BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("memberFindDto", memberService.findMemberFindDtoByMemberId(memberId));
            return "members/memberUpdateForm";
        }

        Long savedMemberId = memberService.updateMember(MemberUpdateDto.builder()
                .id(memberId)   //나중에 memberId 검증할 것
                .name(memberUpdateForm.getName())
                .address(Address.builder().
                        city(memberUpdateForm.getCity())
                        .street(memberUpdateForm.getStreet())
                        .zipcode(memberUpdateForm.getZipcode())
                        .build())
                .build());

        return "redirect:/members/"+savedMemberId;
    }
}