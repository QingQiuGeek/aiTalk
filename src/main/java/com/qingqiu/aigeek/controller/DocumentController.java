package com.qingqiu.aigeek.controller;

import static com.qingqiu.aigeek.convert.ContentConvert.convertToRecord0;
import static dev.langchain4j.data.document.Document.FILE_NAME;
import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.qingqiu.aigeek.enums.BusinessExceptionEnum;
import com.qingqiu.aigeek.exception.BusinessException;
import com.qingqiu.aigeek.domain.vo.RetrievedRecordResponse;
import com.qingqiu.aigeek.util.BR;
import com.qingqiu.aigeek.util.R;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.loader.UrlDocumentLoader;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


/**
 * @link{com.qingqiu.aigeek.rag.ContentRetrievers}
 *  注意！！！RAG检索的embeddingStore和文档加载的embeddingStore要配置相同
 */
@RestController
@Slf4j
@RequestMapping("/document")
public class DocumentController {

    /**
     * 文档切分器
     */
    @Resource
    private DocumentSplitter documentSplitter;

    /**
     * 文档解析器
     */
    @Resource
    private DocumentParser apacheTikaDocumentParser;

    /**
     * 向量模型
     */
    @Resource
    private EmbeddingModel embeddingModel;

    @Autowired
    @Qualifier("pgVectorEmbeddingStore")
    PgVectorEmbeddingStore pgVectorEmbeddingStore;

    @Autowired
    @Qualifier("inMemoryEmbeddingStore")
    InMemoryEmbeddingStore<TextSegment> inMemoryEmbeddingStore;

//    @Resource
//    private EmbeddingStore<TextSegment> pgVectorEmbeddingStore;

    /**
     * 从ClassPath加载文档并向量化存储
     * @return
     */
    @PostMapping("/load/resource")
    @Deprecated
    public BR<String> resourceDocumentEmbeddingAndStore() {
        List<Document> documents = FileSystemDocumentLoader.loadDocuments("src/main/resources/document",apacheTikaDocumentParser);
        List<String> ids = handleDocument(documents);
        return R.ok(StrUtil.format("上传成功，将{}个文档，切分为:{}个段存入向量库", ids.size(), ids.size()));
    }

    /**
     * 从文件加载文档并向量化存储
     *
     * @param files 文件列表
     * @return 存储结果
     * @throws IOException
     */
    @PostMapping("/load/file")
    public BR<String> fileDocumentEmbeddingAndStore(@RequestParam MultipartFile... files) throws IOException {
        List<Document> documentList = Arrays.stream(files).map(file -> {
            try {
                Document document = apacheTikaDocumentParser.parse(file.getInputStream());
                document.metadata().put("file_id", "1");
                document.metadata().put(FILE_NAME, file.getOriginalFilename());
                //保存额外的字段（到元数据中），根据自己的业务需求添加，例如此处保存的是文档的权限字段
                document.metadata().put("scope", 1);
                return document;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toList();
        List<String> ids = handleDocument(documentList);
        return R.ok(StrUtil.format("上传成功，将{}个文档，切分为:{}个段存入向量库", ids.size(), ids.size()));
    }

    /**
     * 从URL加载文档并向量化存储
     *
     * @param fileUrls 文件url列表
     * @return 入库结果
     */
    @PostMapping("/load/url")
    public BR<String> urlDocumentEmbeddingAndStore(@RequestParam("fileUrls") List<String> fileUrls) {
        //todo 判断文档是否存在，如果存在需要先从向量数据库中删除相关记录，再重新入库
        // 计划：使用mysql存储：文件名，url，hash值，向量数据库记录ids，其它业务字段
        List<Document> documentList = fileUrls.stream().map(
                        fileUrl -> {
                            Document document = UrlDocumentLoader.load(URLUtil.encode(fileUrl, StandardCharsets.UTF_8), apacheTikaDocumentParser);
                            document.metadata().put("file_id", "1");
                            document.metadata().put(FILE_NAME, FileUtil.getName(fileUrl));
                            //保存额外的字段（到元数据中），根据自己的业务需求添加，例如此处保存的是文档的权限字段和文件id
                            document.metadata().put("scope", 0);
                            return document;
                        })
                .toList();
        List<String> ids = handleDocument(documentList);
        return R.ok(StrUtil.format("上传成功，将{}个文档，切分为:{}个段存入向量库", ids.size(), ids.size()));
    }

    /**
     * 把文件切分转化成向量，存储到embeddingModel
     * @param documents
     */
    @Deprecated
    public void handleFile(List<Document> documents){
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
            .documentSplitter(documentSplitter)
            // 将文档分割成多个段落TextSegment,同时为了提高搜索质量，为每个 TextSegment 添加文档名称
            .textSegmentTransformer(textSegment -> TextSegment.from(
                textSegment.metadata().getString("segment_name") + "\n" + textSegment.text(),
                textSegment.metadata()
            ))
            // 使用指定的向量模型
            .embeddingModel(embeddingModel)
            //指定存储在pgVector
            .embeddingStore(inMemoryEmbeddingStore)
            .build();
        // 加载到嵌入存储中
        ingestor.ingest(documents);
        log.info("切分{}个文档，存储到向量存储中", documents.size());
    }

    public List<String> handleDocument(List<Document> documents){
        //切分文档，此处是根据段落
        List<TextSegment> segments = documentSplitter.splitAll(documents);
        //将段落向量化
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
        //将向量和段落（包含元数据）存入向量库
        List<String> ids = inMemoryEmbeddingStore.addAll(embeddings, segments);
        if(CollUtil.isEmpty(ids)){
            throw new BusinessException(BusinessExceptionEnum.FILE_UPLOAD_ERROR.getCode(),BusinessExceptionEnum.FILE_UPLOAD_ERROR.getMessage());
        }
        return ids;
    }

    /**
     * 从向量库中查询
     * @param query 查询文本
     * @return 命中文本
     */
    @GetMapping("/query")
    public Set<RetrievedRecordResponse> searchFromEmbeddingStore(@RequestParam("query") String query) {
        //先将原始查询向量化
        Embedding queryEmbedding = embeddingModel.embed(query).content();
//        List<EmbeddingMatch<TextSegment>> relevants = embeddingStore.findRelevant(queryEmbedding, 10);
        EmbeddingSearchResult<TextSegment> search = inMemoryEmbeddingStore.search(
                EmbeddingSearchRequest.builder()
                        .minScore(0.6)
                        .maxResults(10)
                        //根据业务需求，自行添加过滤条件，例如此处：知识库中的文档有权限限制，根据每个人的权限查询出不同的文档
                        .filter(metadataKey("scope").isGreaterThanOrEqualTo(0))
                        .queryEmbedding(queryEmbedding).build());
        List<TextSegment> textSegments = search.matches().stream().map(EmbeddingMatch::embedded).toList();
        return convertToRecord0(textSegments);
    }

}
