package com.kosa.realestate.favorites.controller;

import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kosa.realestate.favorites.model.dto.FavoriteListDTO;
import com.kosa.realestate.favorites.service.IFavoriteService;
import com.kosa.realestate.users.model.UserDTO;
import com.kosa.realestate.users.service.IUserService;
import lombok.RequiredArgsConstructor;

/**
 * FavoriteController
 *
 * @author 오동건
 */
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

    favoriteService.findFavoriteDetailList(page, realEstateId);

    return "favorite_list";
  }

  
  // 즐겨찾기 추가
  @PostMapping("/{realEstateId}")
  @ResponseBody
  public ResponseEntity<?> favoriteAdd(Principal principal, @PathVariable("realEstateId") Long realEstateId) {
    
    try {

      String result = favoriteService.addFavorite(realEstateId, principal.getName());
      return ResponseEntity.ok().body(result);
      
    } catch (Exception e) {
      
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("즐겨찾기 추가에 실패했습니다.");
    }
  }


  // 즐겨찾기 삭제
  @DeleteMapping("/{favoriteId}")
  public String favoriteRemove(Principal principal, @PathVariable("favoriteId") Long favoriteId) {

    favoriteService.removeFavorite(favoriteId);

    return "favorite_list :: content";
  }
}
