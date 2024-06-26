package com.kosa.realestate.users;

import com.kosa.realestate.users.model.UserDTO;
import com.kosa.realestate.users.model.Users;

/**
 * UserMapper 클래스
 *
 * @author 이주윤
 */
public class UserMapper {
  public static Users toEntity(UserDTO userDTO) {
    if(userDTO == null) {
      return null;
    }
    
    return Users.builder()
        .userId(userDTO.getUserId())
        .email(userDTO.getEmail())
        .nickname(userDTO.getNickname())
        .isDeleted(userDTO.getIsDeleted())
        .createdAt(userDTO.getCreatedAt())
        .updatedAt(userDTO.getUpdatedAt())
        .accountType(userDTO.getAccountType())
        .build();
  }
  
  public static UserDTO toDTO(Users user) {
    if (user == null) {
      return null;
    }
    
    return UserDTO.builder()
        .userId(user.getUserId())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .isDeleted(user.getIsDeleted())
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .accountType(user.getAccountType())
        .build();
  }
}
