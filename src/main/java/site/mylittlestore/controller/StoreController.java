package site.mylittlestore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import site.mylittlestore.domain.Address;
import site.mylittlestore.dto.member.MemberFindDto;
import site.mylittlestore.dto.member.MemberUpdateDto;
import site.mylittlestore.dto.store.StoreDto;
import site.mylittlestore.dto.store.StoreUpdateDto;
import site.mylittlestore.form.MemberUpdateForm;
import site.mylittlestore.form.StoreCreationForm;
import site.mylittlestore.form.StoreUpdateForm;
import site.mylittlestore.service.ItemService;
import site.mylittlestore.service.MemberService;
import site.mylittlestore.service.StoreService;
import site.mylittlestore.service.OrderService;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class StoreController {

    private final MemberService memberService;
    private final StoreService storeService;
    private final OrderService orderService;

    private final ItemService itemService;

    @GetMapping("/members/{memberId}/stores/{storeId}")
    public String storeInfo(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId, Model model) {
        model.addAttribute("memberId", memberId);
        model.addAttribute("storeId", storeId);
        model.addAttribute("storeDto", storeService.findStoreDtoById(storeId));

        return "stores/storeInfo";
    }

    @GetMapping("/members/{memberId}/stores/new")
    public String createStoreForm(@PathVariable("memberId") Long memberId, Model model) {
        model.addAttribute("memberId", memberId);
        model.addAttribute("storeCreationForm", new StoreCreationForm());

        return "stores/storeCreationForm";
    }

    @PostMapping("/members/{memberId}/stores/new")
    public String createStore(@PathVariable("memberId") Long memberId, @Valid StoreCreationForm storeCreationForm, BindingResult result) {

        if (result.hasErrors()) {
            return "stores/storeCreationForm";
        }

        Long createdStoreId = memberService.createStore(StoreDto.builder()
                .memberId(memberId)
                .name(storeCreationForm.getName())
                .address(Address.builder()
                        .city(storeCreationForm.getCity())
                        .street(storeCreationForm.getStreet())
                        .zipcode(storeCreationForm.getZipcode())
                        .build())
                .build());

        return "redirect:/members/"+memberId+"/stores/"+createdStoreId;
    }

    @GetMapping("/members/{memberId}/stores/{storeId}/update")
    public String updateStoreForm(@PathVariable("storeId") Long storeId, Model model) {
        model.addAttribute("storeFindDto", storeService.findStoreDtoById(storeId));
        model.addAttribute("storeUpdateForm", new StoreUpdateForm());

        return "stores/storeUpdateForm";
    }

    @PostMapping("/members/{memberId}/stores/{storeId}/update")
    public String updateStore(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId, @Valid StoreUpdateForm storeUpdateForm, BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("storeFindDto", storeService.findStoreDtoById(storeId));
            model.addAttribute("storeUpdateForm", new StoreUpdateForm());
            return "stores/storeUpdateForm";
        }

        Long updatedStoreId = memberService.updateStore(StoreUpdateDto.builder()
                .memberId(memberId) //나중에 memberId 검증할 것
                .id(storeId) //나중에 storeId 검증할 것
                .newName(storeUpdateForm.getName())
                .newAddress(Address.builder()
                        .city(storeUpdateForm.getCity())
                        .street(storeUpdateForm.getStreet())
                        .zipcode(storeUpdateForm.getZipcode())
                        .build())
                .build());

        return "redirect:/members/"+memberId+"/stores/"+updatedStoreId;
    }
    
    @GetMapping("/members/{memberId}/stores/{storeId}/changeStoreStatus")
    public String changeStoreStatus(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId) {
        memberService.changeStoreStatus(StoreUpdateDto.builder()
                .id(storeId)
                .memberId(memberId)
                .build());

        return "redirect:/members/"+memberId+"/stores/"+storeId;
    }
}
