package life.cookedfox.bookmarkenhance.lucene;

import io.micrometer.common.util.StringUtils;
import life.cookedfox.bookmarkenhance.constant.ApplicationConstants;
import life.cookedfox.bookmarkenhance.model.Bookmark;
import life.cookedfox.bookmarkenhance.model.Page;
import life.cookedfox.bookmarkenhance.utils.LambdaUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class IndexEngine {

    @Value("${lucene.index.path}")
    String indexPath;

    @SneakyThrows
    public void addDocument(Bookmark bookmark) {
        FSDirectory directory = FSDirectory.open(Paths.get(indexPath));
        PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new IKAnalyzer(), Map.of(LambdaUtils.name(Bookmark::getAiTagList), new WhitespaceAnalyzer()));

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        try (IndexWriter indexWriter = new IndexWriter(directory, config)) {
            Document document = new Document();
            document.add(new StringField(LambdaUtils.name(Bookmark::getId), UUID.randomUUID().toString(), Field.Store.YES));
            document.add(new StringField(LambdaUtils.name(Bookmark::getUrl), bookmark.getUrl(), Field.Store.YES));
            document.add(new TextField(LambdaUtils.name(Bookmark::getAiSummary), bookmark.getAiSummary(), Field.Store.YES));
            document.add(new TextField(LambdaUtils.name(Bookmark::getContent), bookmark.getContent(), Field.Store.YES));
            document.add(new TextField(LambdaUtils.name(Bookmark::getAiTagList), String.join(" ", bookmark.getAiTagList()), Field.Store.YES));
            document.add(new StringField(LambdaUtils.name(Bookmark::getSnapshotUrl), bookmark.getSnapshotUrl(), Field.Store.YES));
            document.add(new StringField(LambdaUtils.name(Bookmark::getCreateTime), LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME), Field.Store.YES));
            indexWriter.addDocument(document);
        }
    }

    @SneakyThrows
    public Page<Bookmark> search(String keyword, String... fields) {
        Map.Entry<Integer, Integer> pageInfo = ApplicationConstants.pageNumberAndPageSizeThreadLocal.get();
        ApplicationConstants.pageNumberAndPageSizeThreadLocal.remove();
        Integer pageNumber = pageInfo.getKey();
        Integer pageSize = pageInfo.getValue();

        if (StringUtils.isNotBlank(keyword)) {
            keyword = QueryParser.escape(keyword.trim());
        } else {
            return Page.of(pageNumber, pageSize, 0, List.of());
        }
        FSDirectory directory = FSDirectory.open(Paths.get(indexPath));
        Analyzer analyzer = new IKAnalyzer();

        try (DirectoryReader directoryReader = DirectoryReader.open(directory)) {
            IndexSearcher indexSearcher = new IndexSearcher(directoryReader);

            BooleanQuery.Builder builder = new BooleanQuery.Builder();
            for (String field : fields) {
                QueryParser fieldQueryParser = new QueryParser(field, analyzer);
                Query fieldQuery = fieldQueryParser.parse(keyword);
                builder.add(fieldQuery, BooleanClause.Occur.SHOULD);
            }
            BooleanQuery query = builder.build();

            int start = (pageNumber - 1) * pageSize;
            TopDocs topDocs = indexSearcher.search(query, start + pageSize);
            int totalHits = Math.toIntExact(topDocs.totalHits.value);

            // 设置高亮
            SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<b>", "</b>");
            QueryScorer scorer = new QueryScorer(query);
            Highlighter highlighter = new Highlighter(formatter, scorer);
            highlighter.setTextFragmenter(new SimpleSpanFragmenter(scorer));

            List<Bookmark> collect = Arrays.stream(topDocs.scoreDocs).map(e -> {
                try {
                    Document document = indexSearcher.doc(e.doc);

                    Map<String, String> highlight = new HashMap<>();
                    for (String field : fields) {
                        String content = document.get(field);
                        if (StringUtils.isNotBlank(content)) {
                            String highlightedContent = highlighter.getBestFragment(analyzer, LambdaUtils.name(Bookmark::getAiSummary), content);
                            highlight.put(field, highlightedContent);
                        }
                    }
                    return Bookmark.builder()
                            .id(document.get(LambdaUtils.name(Bookmark::getId)))
                            .url(document.get(LambdaUtils.name(Bookmark::getUrl)))
                            .snapshotUrl(document.get(LambdaUtils.name(Bookmark::getSnapshotUrl)))
                            .aiSummary(document.get(LambdaUtils.name(Bookmark::getAiSummary)))
                            .highlight(highlight)
                            .createTime(LocalDateTime.parse(document.get(LambdaUtils.name(Bookmark::getCreateTime))))
                            .aiTagList(List.of(document.get(LambdaUtils.name(Bookmark::getAiTagList)).split(" ")))
                            .build();
                } catch (Exception ex) {
                    log.error("", ex);
                    throw new RuntimeException(ex);
                }
            }).collect(Collectors.toList());
            return Page.of(pageNumber, pageSize, totalHits, collect);
        }
    }

    @SneakyThrows
    public List<Bookmark> termSearch(String term, String field) {
        if (StringUtils.isNotBlank(term)) {
            term = term.trim();
        } else {
            return List.of();
        }
        FSDirectory directory = FSDirectory.open(Paths.get(indexPath));
        try (DirectoryReader directoryReader = DirectoryReader.open(directory)) {
            IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
            Query query = new TermQuery(new Term(field, term));
            TopDocs topDocs = indexSearcher.search(query, Integer.MAX_VALUE);
            return Arrays.stream(topDocs.scoreDocs).map(e -> {
                try {
                    Document document = indexSearcher.doc(e.doc);
                    return Bookmark.builder()
                            .id(document.get(LambdaUtils.name(Bookmark::getId)))
                            .url(document.get(LambdaUtils.name(Bookmark::getUrl)))
                            .snapshotUrl(document.get(LambdaUtils.name(Bookmark::getSnapshotUrl)))
                            .aiSummary(document.get(LambdaUtils.name(Bookmark::getAiSummary)))
                            .createTime(LocalDateTime.parse(document.get(LambdaUtils.name(Bookmark::getCreateTime))))
                            .aiTagList(List.of(document.get(LambdaUtils.name(Bookmark::getAiTagList)).split(" ")))
                            .build();
                } catch (Exception ex) {
                    log.error("", ex);
                    throw new RuntimeException(ex);
                }
            }).collect(Collectors.toList());
        }
    }

    @SneakyThrows
    public void deleteDoc(String term, String field) {
        FSDirectory directory = FSDirectory.open(Paths.get(indexPath));
        try (IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(null))) {
            writer.deleteDocuments(new Term(field, term));
            writer.commit();
        }
    }
}
