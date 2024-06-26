package com.kosa.realestate.users.controller;

import com.kosa.realestate.favorites.model.dto.FavoriteListDTO;
import com.kosa.realestate.favorites.service.FavoriteService;
import com.kosa.realestate.users.DuplicateUserException;
import com.kosa.realestate.users.InvalidPasswordException;
import com.kosa.realestate.users.form.UserCreateForm;
import com.kosa.realestate.users.form.UserUpdateForm;
import com.kosa.realestate.users.model.UserDTO;
import com.kosa.realestate.users.service.IUserService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * UserController 클래스
 *
 * @author 이주윤
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
    return "signup_form";
  }

  // 회원가입 폼 저장
  @PostMapping("/signup")
  public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return "signup_form";
    }

    // 확인 비밀번호 불일치
    if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
      bindingResult.rejectValue("password2", "passwordInCorrect", "패스워드가 일치하지 않습니다.");
      return "signup_form";
    }

    // 중복 가입 방지
    try {
      userService.createUser(userCreateForm.getEmail(), userCreateForm.getPassword1(),
          userCreateForm.getNickname());
    } catch (DuplicateUserException e) {
      if (e.getMessage().contains("Email")) {
        bindingResult.rejectValue("email", "emailDuplicate", "사용 중인 이메일입니다.");
      } else if (e.getMessage().contains("Nickname")) {
        bindingResult.rejectValue("nickname", "nicknameDuplicate", "사용 중인 닉네임입니다.");
      }
      return "signup_form";
    }

    // TODO: redirect 위치를 메인 페이지로 변경하기
    return "redirect:/";
  }

  // 로그인 페이지로 이동
  @GetMapping("/login")
  public String login() {
    return "login_form";
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
    model.addAttribute("favoriteListDtoList", favoriteListDtoList);

    return "my_page_temp";
  }

  // 회원 정보 수정 페이지로 이동
  @GetMapping("/me/update")
  public String updateUser(Principal principal, UserUpdateForm userUpdateForm) {
    if (principal == null) {
      return "access_denied";
    }

    return "update_form";
  }

  // 회원 정보 수정 폼 저장
  @PutMapping("/me")
  @ResponseBody
  public ResponseEntity<?> updateUser(@Valid @RequestBody UserUpdateForm userUpdateForm, BindingResult bindingResult,
      Principal principal) {

    Map<String, String> error = new HashMap<>();
    Map<String, String> success = new HashMap<>();

    if(bindingResult.hasErrors()) {
      error.put("message", "Validation 실패");
      return ResponseEntity.badRequest().body(error);
    }

    if(!userUpdateForm.getPassword1().equals(userUpdateForm.getPassword2())) {
      error.put("message", "비밀번호가 일치하지 않습니다.");
      return ResponseEntity.badRequest().body(error);
    }

    String email = principal.getName();

    boolean isUpdated = userService.updateUser(email, userUpdateForm.getPassword1());

    if(isUpdated) {
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


      if(isDeleted) {
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
}