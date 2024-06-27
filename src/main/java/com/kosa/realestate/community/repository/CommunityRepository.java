package com.kosa.realestate.community.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.kosa.realestate.community.dto.FileMetaDataDTO;
import com.kosa.realestate.community.dto.PostDTO;

@Mapper
@Repository
public interface CommunityRepository {
	
	public int insertPost(PostDTO pdto);

	public void fileUpload(FileMetaDataDTO fdto);

	public List<PostDTO> postList();

	public PostDTO communityCard(int postId);
}
