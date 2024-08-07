package com.kosa.realestate.community.dto;

import java.time.LocalDateTime;

import com.kosa.realestate.admindivision.model.dto.DistrictDTO;
import com.kosa.realestate.users.model.UserDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * PostDTO 클래스
 * 
 * @author 강희원
 */

@Getter
@Setter
@ToString
@RequiredArgsConstructor

public class PostDTO {

	private Long postId;
    private int userId;
    private int districtId;
    private String title;
    private String content;
    private int postView;
    private String isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String nickname;
    private String districtName;
    private String dateOnly;
    

    
    // 모든 필드를 초기화하는 생성자
    public PostDTO(Long postId, int userId, int districtId, String title, String content,
                   int postView, String isDeleted, LocalDateTime createdAt, LocalDateTime updatedAt,
                   String nickname, String districtName) {
        this.postId = postId;
        this.userId = userId;
        this.districtId = districtId;
        this.title = title;
        this.content = content;
        this.postView = postView;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.nickname = nickname;
        this.districtName = districtName;
    }

	public PostDTO(int userId, int districtId, String title, String content) {
		super();
		this.userId = userId;
		this.districtId = districtId;
		this.title = title;
		this.content = content;
	}
    

}

