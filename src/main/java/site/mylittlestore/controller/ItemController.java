package site.mylittlestore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import site.mylittlestore.dto.item.ItemCreationDto;
import site.mylittlestore.dto.item.ItemFindDto;
import site.mylittlestore.dto.item.ItemUpdateDto;
import site.mylittlestore.form.ItemCreationForm;
import site.mylittlestore.form.ItemUpdateForm;
import site.mylittlestore.service.ItemService;
import site.mylittlestore.service.StoreService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final StoreService storeService;

    private final ItemService itemService;

    @GetMapping("/members/{memberId}/stores/{storeId}/items")
    public String itemList(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId, Model model) {
        List<ItemFindDto> findAllItemFindDtoByStoreId = itemService.findAllItemDtoByStoreId(storeId);
        model.addAttribute("memberId", memberId);
        model.addAttribute("itemDtoList", findAllItemFindDtoByStoreId);

        return "items/itemList";
    }

    @GetMapping("/members/{memberId}/stores/{storeId}/items/{itemId}")
    public String itemInfo(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId, @PathVariable("itemId") Long itemId, Model model) {
        model.addAttribute("memberId", memberId);
        model.addAttribute("storeId", storeId);
        model.addAttribute("itemId", itemId);
        model.addAttribute("itemFindDto", itemService.findItemDtoById(itemId));

        return "items/itemInfo";
    }

    @GetMapping("/members/{memberId}/stores/{storeId}/items/new")
    public String createItemForm(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId, Model model) {
        model.addAttribute("memberId", memberId);
        model.addAttribute("storeId", storeId);
        model.addAttribute("itemCreationForm", new ItemCreationForm());

        return "items/itemCreationForm";
    }

    @PostMapping("/members/{memberId}/stores/{storeId}/items/new")
    public String createItem(@PathVariable Long memberId, @PathVariable Long storeId, @Valid ItemCreationForm itemCreationForm, BindingResult result) {

        if (result.hasErrors()) {
            return "items/itemCreationForm";
        }

        Long createdItemId = storeService.createItem(ItemCreationDto.builder()
                .storeId(storeId)
                .name(itemCreationForm.getName())
                .price(itemCreationForm.getPrice())
                .stock(itemCreationForm.getStock())
                .build());

        return "redirect:/members/" + memberId + "/stores/" + storeId + "/items/";
    }

    @GetMapping("/members/{memberId}/stores/{storeId}/items/{itemId}/update")
    public String updateItemForm(@PathVariable("memberId") Long memberId, @PathVariable("itemId") Long itemId, Model model) {
        model.addAttribute("memberId", memberId);
        model.addAttribute("itemFindDto", itemService.findItemDtoById(itemId));
        model.addAttribute("itemUpdateForm", new ItemUpdateForm());

        return "items/itemUpdateForm";
    }

    @PostMapping("/members/{memberId}/stores/{storeId}/items/{itemId}/update")
    public String updateItem(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId, @PathVariable("itemId") Long itemId, @Valid ItemUpdateForm itemUpdateForm, BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("itemFindDto", itemService.findItemDtoById(storeId));
            return "items/itemUpdateForm";
        }

        Long updatedItemId = storeService.updateItem(ItemUpdateDto.builder()
                .id(itemId) //나중에 itemId 검증할 것
                .storeId(storeId) //나중에 storeId 검증할 것
                .newItemName(itemUpdateForm.getName())
                .newPrice(itemUpdateForm.getPrice())
                .newStock(itemUpdateForm.getStock())
                .build());

        return "redirect:/members/"+memberId+"/stores/"+storeId+"/items/";
    }

    @GetMapping("/members/{memberId}/stores/{storeId}/items/{itemId}/delete")
    public String deleteItem(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId, @PathVariable("itemId") Long itemId) {
        itemService.deleteItemById(itemId);

        return "redirect:/members/"+memberId+"/stores/"+storeId+"/items";
    }
}
