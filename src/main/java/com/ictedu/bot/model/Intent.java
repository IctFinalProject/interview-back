package com.ictedu.bot.model;

import java.util.List;

import lombok.*;

@Setter
@Getter
public class Intent {
	 private String tag;
	 private List<String> patterns;
	 private List<String> responses;
}
