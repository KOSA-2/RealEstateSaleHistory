package com.kosa.realestate.realestates.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kosa.realestate.realestates.model.RealEstateWithSale;
import com.kosa.realestate.realestates.service.IRealEstateSaleService;
import lombok.RequiredArgsConstructor;
import oracle.jdbc.proxy.annotation.Post;

@Controller
@RequiredArgsConstructor
@RequestMapping("/real-estate")
public class RealEstateSaleController {

  private final IRealEstateSaleService realEstateSaleService;

  @GetMapping("/count")
  public String estateCount(
      @RequestParam(value = "realEstateId", required = false, defaultValue = "0") int realEstateId,
      Model model) {
    if (realEstateId == 0) {
      model.addAttribute("count",realEstateSaleService.getRealEstateSaleCount());
    } 
    else {
      model.addAttribute("count",realEstateSaleService.getRealEstateSaleCount(realEstateId));
    }
    return "estate/count";
  }
  
  @GetMapping("/select")
  public String estateSelect(Model model) {
     model.addAttribute("districtList",realEstateSaleService.getAllDestrictId());
//     model.addAttribute("",realEstateSaleService.)
    return "estate/map";
  }
  
  @GetMapping("/search")
  @ResponseBody
  public List<RealEstateWithSale> getEstateList(

      ){
    return realEstateSaleService.selectRealEstateWithSales(0,1,10);
  }
}
