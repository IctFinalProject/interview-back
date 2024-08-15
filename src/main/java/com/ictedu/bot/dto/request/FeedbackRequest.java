package com.ictedu.bot.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FeedbackRequest {
	 private Long answerId;
	 private boolean isLike;
}
