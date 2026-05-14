package com.qingqiu.openchat.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryModelProviderRequest {

	private Long id;

	private Long userId;

	private String modelName;

	private String providerType;

}
