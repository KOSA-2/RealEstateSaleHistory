package com.kosa.realestate.users.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.exceptions.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.kosa.realestate.favorites.model.dto.FavoriteListDTO;
import com.kosa.realestate.favorites.service.FavoriteService;
import com.kosa.realestate.DuplicateUserException;
import com.kosa.realestate.InvalidPasswordException;
import com.kosa.realestate.users.form.UserCreateForm;
import com.kosa.realestate.users.form.UserUpdateForm;
import com.kosa.realestate.users.model.UserDTO;
import com.kosa.realestate.users.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * UserController 클래스
 *
 * @author 이주윤, 백재우 오동건
 */

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

  private static final Logger log = LoggerFactory.getLogger(UserController.class);
  private final IUserService userService;
  private final FavoriteService favoriteService;

  // 회원가입 페이지로 이동
  @GetMapping("/signup")
  public String signup(UserCreateForm userCreateForm) {

    return "signup";
  }

  // 회원가입
  @PostMapping("/signup")
  public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
    if (bindingResult.hasErrors()) {
      return "signup";
    }

    // 확인 비밀번호 불일치
    if (!userCreateForm.getPassword().equals(userCreateForm.getPasswordConfirm())) {
      bindingResult.rejectValue("passwordConfirm", "passwordInCorrect", "패스워드가 일치하지 않습니다.");
      return "signup";
    }

    // 중복 가입 방지
    try {
      userService.createUser(userCreateForm.getEmail(), userCreateForm.getPassword(),
          userCreateForm.getNickname());
    } catch (DuplicateUserException e) {
      if (e.getMessage().contains("Email")) {
        bindingResult.rejectValue("email", "emailDuplicate", "사용 중인 이메일입니다.");
      } else if (e.getMessage().contains("Nickname")) {
        bindingResult.rejectValue("nickname", "nicknameDuplicate", "사용 중인 닉네임입니다.");
      }
      return "signup";
    }

    redirectAttributes.addFlashAttribute("signupSuccess", true);
    return "redirect:/";
  }

  // 로그인 페이지로 이동
  @GetMapping("/login")
  public String login() {
    return "login";
  }

  // 마이 페이지로 이동
  @GetMapping("/me")
  public String myPage(Principal principal, Model model) {
    if (principal == null) {
      return "access_denied";
    }

    String email = principal.getName();
    UserDTO userDTO = userService.findUserByEmail(email);

    model.addAttribute("user", userDTO);

    List<FavoriteListDTO> favoriteListDtoList = favoriteService.findFavoriteList(0, email);
    model.addAttribute("favoriteList", favoriteListDtoList);

    // TODO: my_page.html에서 AJAX 도입해서 페이지네이션
    return "my-page";
  }

  // 회원 정보 수정
  @PutMapping("/me")
  @ResponseBody
  public ResponseEntity<?> updateUser(@Valid @RequestBody UserUpdateForm userUpdateForm,
      BindingResult bindingResult,
      Principal principal) {

    Map<String, String> error = new HashMap<>();
    Map<String, String> success = new HashMap<>();

    if (bindingResult.hasErrors()) {
      error.put("message", "Validation 실패");
      return ResponseEntity.badRequest().body(error);
    }

    if (!userUpdateForm.getPassword().equals(userUpdateForm.getPasswordConfirm())) {
      error.put("message", "비밀번호가 일치하지 않습니다.");
      return ResponseEntity.badRequest().body(error);
    }

    String email = principal.getName();

    boolean isUpdated = userService.updateUser(email, userUpdateForm.getPassword());

    if (isUpdated) {
      success.put("message", "회원정보가 성공적으로 수정되었습니다.");
      return ResponseEntity.ok(success);
    } else {
      error.put("message", "원인 불명");
      return ResponseEntity.status(500).body(error);
    }
  }

  // 회원 탈퇴
  @DeleteMapping("/me")
  @ResponseBody
  public ResponseEntity<?> deleteUser(Principal principal, @RequestBody UserDTO userDTO) {
    String email = principal.getName();
    String password = userDTO.getPassword();
    Map<String, String> response = new HashMap<>();

    try {
      boolean isDeleted = userService.deleteUser(email, password);

      if (isDeleted) {
        response.put("message", "계정이 삭제되었습니다.");
        return ResponseEntity.ok(response);
      } else {
        response.put("message", "원인 불명");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
      }
    } catch (UsernameNotFoundException e) {
      response.put("message", e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    } catch (InvalidPasswordException e) {
      response.put("message", e.getMessage());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
  }

  // id로 회원조회(테스트)
  @GetMapping("/{userId}")
  @ResponseBody
  public UserDTO getUserById(@PathVariable("userId") Long userId) {
    return userService.getUserById(userId);
  }

  // 전체 회원 조회(테스트)
  @GetMapping("/")
  @ResponseBody
  public List<UserDTO> getUserList() {
    return userService.getUserList();
  }


  //사용자 중개자 권한 요청
  @PostMapping("/me/permission")
  @ResponseBody
  public ResponseEntity<?> requestPermission(Principal principal,
      RedirectAttributes redirectAttributes) {
    if (principal == null) {
      return ResponseEntity.status(403).body("접근 거부됨");
    }

    Map<String, String> response = new HashMap<>();
    String email = principal.getName();
    UserDTO target = userService.findUserByEmail(email);

    try {
      boolean result = userService.requestPermission(target.getUserId());

      if (result) {
        response.put("message", "중개사 권한 요청이 완료되었습니다.");
        return ResponseEntity.ok(response);
      } else {
        response.put("message", "중개사 권한 요청에 실패했습니다.");
        return ResponseEntity.badRequest().body(response);
      }
    } catch (PersistenceException e) {
      response.put("message", e.getMessage());
      return ResponseEntity.badRequest().body(response);
    } catch (Exception e) {
      response.put("message", e.getMessage());
      return ResponseEntity.badRequest().body(response);
    }
  }

  //관리자가 권한요청 리스트를 조회
  @GetMapping("/admin/requestlist/{startPage}")
  @ResponseBody //테스트용
  public String selectUpgradeRequests(@PathVariable("startPage") int startPage, Model model) {
    List<Map<String, Object>> list = userService.selectUpgradeRequests(startPage, 10);
    model.addAttribute("list", list);
    return list.toString(); // 뷰페이지 생성하기
  }

  //중개자 리시트 조회
  @GetMapping("/admin/search")
  @ResponseBody //테스트용
  public String searchAgentList(@RequestParam(value = "nickname", required = false) String nickname,
      @RequestParam(value = "startNum", defaultValue = "1") int startNum, Model model) {
    List<UserDTO> list = userService.searchAgentList(nickname, startNum);
    model.addAttribute("list", list);
    // model.addAttribute("startNum",startNum); 향후 필요할 수도 있음
    return list.toString(); // 뷰페이지 생성하기
  }

  //중개자 권한 UPDATE
  @PostMapping("/updateUserType")
  public ResponseEntity<?> updateUserType(@RequestBody List<Long> userIds) {
    int updatedCount = userService.updateUserAccountType(userIds);
    return ResponseEntity.ok("성공적으로 업데이트된 사용자 수: " + updatedCount);
  }


  //권한 요청 거절 
  @PostMapping("/rejectPermission")
  public ResponseEntity<?> rejectPermission(@RequestBody List<Long> userId) {

    boolean success = userService.rejectUserAccountType(userId);

    if (success) {
      return ResponseEntity.ok("사용자 권한 요청이 성공적으로 거절되었습니다.");
    } else {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("권한 요청 거절 처리 중 오류가 발생했습니다.");
    }
  }

  //권한 UPDATE agent -> nomal
  @PostMapping("/roleToNomal")
  public ResponseEntity<?> updateRoleToNormal(@RequestBody List<Long> userIds) {

    int updatedCount = userService.updateRoleToNormal(userIds);
    return ResponseEntity.ok("성공적으로 업데이트된 사용자 수: " + updatedCount);
  }

}