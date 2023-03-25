package site.mylittlestore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import site.mylittlestore.dto.store.StoreTableCreationDto;
import site.mylittlestore.service.StoreTableService;

@Controller
@RequiredArgsConstructor
public class StoreTableController {

    private final StoreTableService storeTableService;

    @GetMapping("/members/{memberId}/stores/{storeId}/storeTables/new")
    public String createStoreTable(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId) {
        Long savedStoreTableId = storeTableService.createStoreTable(storeId);

        return "redirect:/members/"+memberId+"/stores/"+storeId+"/storeTables/"+savedStoreTableId;
    }

    @GetMapping("/members/{memberId}/stores/{storeId}/storeTables")
    public String storeTableList(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId, Model model) {
        model.addAttribute("memberId", memberId);
        model.addAttribute("storeId", storeId);
        model.addAttribute("StoreTableDtoWithOrderIdList", storeTableService.findAllStoreTableFindDtoWithOrderFindDtoByStoreId(storeId));

        return "storeTables/StoreTableList";
    }
}
