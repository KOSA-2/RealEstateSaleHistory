package com.kosa.realestate.users.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 이주윤
 */
@Getter
@Setter
public class UserCreateForm {
  
  @NotEmpty(message = "이메일 입력은 필수 입니다.")
  @Email(message = "이메일 형식으로 입력해주세요.")
  private String email;
  
  @NotEmpty(message = "비밀번호는 필수항목입니다.")
  private String password;
  
  @NotEmpty(message = "비밀번호 확인은 필수항목입니다.")
  private String passwordConfirm;
  
  @NotEmpty(message = "닉네임은 필수항목입니다.")
  private String nickname;
}
