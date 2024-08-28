package com.ictedu.bbs.service;

import java.time.LocalDateTime;


import com.ictedu.bbs.model.entity.Bbs;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FileBbsDto {
	private Long BbsFileId;
	private Bbs bbs;
	private String fileId;
	private LocalDateTime created_time;

	public FileBbsDto(Long BbsFileId, Bbs bbs, String fileId, LocalDateTime createdTime) {
		this.BbsFileId = BbsFileId;
		this.bbs = bbs;
		this.fileId = fileId;
		this.created_time = createdTime;
	}
}
