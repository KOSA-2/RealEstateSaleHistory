package com.kosa.realestate.favorites.controller;

import com.kosa.realestate.favorites.model.dto.FavoriteListDTO;
import com.kosa.realestate.favorites.service.IFavoriteService;
import com.kosa.realestate.users.model.UserDTO;
import com.kosa.realestate.users.service.IUserService;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/favorites")
public class FavoriteController {

  private final IFavoriteService favoriteService;
  private final IUserService userService;

  // 즐겨찾기 아파트 리스트 조회
  @GetMapping
  public String favoriteList(
      Model model, Principal principal,
      @RequestParam(value = "page", defaultValue = "0") int page) {

    List<FavoriteListDTO> favoriteListDto = favoriteService.findFavoriteList(page,
        principal.getName());

    UserDTO userDTO = userService.findUserByEmail(principal.getName());

    int pageSize = 6;
    int totalFavorites = favoriteService.getTotalFavoritesCountByEmail(userDTO.getUserId());
    int totalPages = (int) Math.ceil((double) totalFavorites / pageSize);

    model.addAttribute("favoriteCount", favoriteListDto.size());
    model.addAttribute("favoriteList", favoriteListDto);
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", totalPages);

    return "favorite_list :: content";
  }


  // 즐겨찾기 아파트 상세 조회
  @GetMapping("/details/{realEstateId}")
  public String favoriteDetailList(
      Model model,
      @PathVariable("realEstateId") Long realEstateId,
      @RequestParam(value = "page", defaultValue = "0") int page) {

    favoriteService.findFavoriteDetailLst(page, realEstateId);

    return "favorite_list";
  }


  // 즐겨찾기 추가
  @PostMapping("/{realEstateId}")
  public String favoriteAdd(Principal principal, @PathVariable("realEstateId") Long realEstateId) {

    favoriteService.addFavorite(realEstateId, "tot0119@naver.com");

    return "favorite_list";
  }


  // 즐겨찾기 삭제
  @DeleteMapping("/{favoriteId}")
  public String favoriteRemove(Principal principal, @PathVariable("favoriteId") Long favoriteId) {

    favoriteService.removeFavorite(favoriteId);

    return "favorite_list";
  }
}
