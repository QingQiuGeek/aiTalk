package com.qingqiu.aigeek.domain.vo;

import java.util.Collection;

/**
 * 检索结果
 */
public record RetrievedRecordResponse(String fileId, String fileName, String url, String absolutePath, Collection<String> texts) {
}
