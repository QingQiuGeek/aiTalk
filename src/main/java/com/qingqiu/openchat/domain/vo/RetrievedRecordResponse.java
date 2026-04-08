package com.qingqiu.openchat.domain.vo;

import java.util.Collection;

/**
 * 检索结果
 */
public record RetrievedRecordResponse(String fileId, String fileName, String url, String absolutePath, Collection<String> texts) {
}
