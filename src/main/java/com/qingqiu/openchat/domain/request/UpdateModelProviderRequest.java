package com.qingqiu.openchat.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateModelProviderRequest {

	private Long id;

	private String modelName;

	private String providerType;

	private String baseUrl;

	private String apiKey;

	private Integer maxTokens;

}
