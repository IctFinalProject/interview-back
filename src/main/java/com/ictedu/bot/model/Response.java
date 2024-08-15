package com.ictedu.bot.model;

import lombok.*;

@Setter
@Getter
public class Response {
	private String message;

	public Response(String message) {
		this.message = message;
	}
	
	
}
