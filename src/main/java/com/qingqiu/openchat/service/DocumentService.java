package com.qingqiu.openchat.service;

import com.qingqiu.openchat.domain.request.CreateDocumentRequest;
import com.qingqiu.openchat.domain.request.UpdateDocumentRequest;
import com.qingqiu.openchat.domain.vo.DocumentVO;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {
    List<DocumentVO> getDocuments();

    List<DocumentVO> getDocumentsByKbId(String kbId);

    Long createDocument(CreateDocumentRequest request);

    Long uploadDocument(String kbId, MultipartFile file);

    void deleteDocument(String documentId);

    void updateDocument(String documentId, UpdateDocumentRequest request);
}
