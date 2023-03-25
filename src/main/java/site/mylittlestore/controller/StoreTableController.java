package site.mylittlestore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import site.mylittlestore.dto.store.StoreTableCreationDto;
import site.mylittlestore.dto.storetable.StoreTableFindDto;
import site.mylittlestore.service.StoreTableService;

@Controller
@RequiredArgsConstructor
public class StoreTableController {

    private final StoreTableService storeTableService;

    @GetMapping("/members/{memberId}/stores/{storeId}/storeTables")
    public String storeTableList(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId, Model model) {
        model.addAttribute("memberId", memberId);
        model.addAttribute("storeId", storeId);
        model.addAttribute("StoreTableDtoWithOrderIdList", storeTableService.findAllStoreTableFindDtoWithOrderFindDtoByStoreId(storeId));

        return "storeTables/StoreTableList";
    }

    @GetMapping("/members/{memberId}/stores/{storeId}/storeTables/{storeTableId}")
    public String storeTableInfo(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId, @PathVariable("storeTableId") Long storeTableId, Model model) {
        StoreTableFindDto storeTableFindDtoById = storeTableService.findStoreTableFindDtoById(storeTableId);

        //주문이 있으면 주문 정보로 리다이렉트
        if (storeTableFindDtoById.getOrderId() != null) {
            return "redirect:/members/" + memberId + "/stores/" + storeId + "/storeTables/" + storeTableId + "/orders/" + storeTableFindDtoById.getOrderId();
        }

        model.addAttribute("memberId", memberId);
        model.addAttribute("storeId", storeId);
        model.addAttribute("storeTableId", storeTableId);
        model.addAttribute("storeTableFindDto", storeTableFindDtoById);
        return "storeTables/StoreTableInfo";
    }

    @GetMapping("/members/{memberId}/stores/{storeId}/storeTables/new")
    public String createStoreTable(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId) {
        Long savedStoreTableId = storeTableService.createStoreTable(storeId);

        return "redirect:/members/"+memberId+"/stores/"+storeId+"/storeTables/"+savedStoreTableId;
    }

}