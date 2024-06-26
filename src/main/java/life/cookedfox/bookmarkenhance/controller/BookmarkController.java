package life.cookedfox.bookmarkenhance.controller;

import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import life.cookedfox.bookmarkenhance.constant.ApplicationConstants;
import life.cookedfox.bookmarkenhance.lucene.IndexEngine;
import life.cookedfox.bookmarkenhance.model.Bookmark;
import life.cookedfox.bookmarkenhance.model.Completion;
import life.cookedfox.bookmarkenhance.model.CompletionRequestParam;
import life.cookedfox.bookmarkenhance.model.Page;
import life.cookedfox.bookmarkenhance.service.ICompletionService;
import life.cookedfox.bookmarkenhance.service.IExtractService;
import life.cookedfox.bookmarkenhance.service.impl.SnapshotService;
import life.cookedfox.bookmarkenhance.utils.LambdaUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@RestController
@RequestMapping("/bookmark")
public class BookmarkController {

    private static final Executor executor = Executors.newFixedThreadPool(3);

    private static final ConcurrentHashMap<String, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    @Resource
    ICompletionService completionService;

    @Resource
    IExtractService extractService;

    @Resource
    IndexEngine indexEngine;

    @Resource
    SnapshotService snapshotService;

    @GetMapping("/deleteDoc")
    public String deleteDoc(@RequestParam String id) {
        indexEngine.deleteDoc(id, LambdaUtils.name(Bookmark::getId));
        return "OK";
    }

    @PostMapping("/addTask")
    public String addTask(@RequestBody Bookmark bookmark) {
        final String url = bookmark.getUrl().trim();

        // 是否已经解析过的url
        List<Bookmark> bookmarks = indexEngine.termSearch(url, LambdaUtils.name(Bookmark::getUrl));
        if (!CollectionUtils.isEmpty(bookmarks)) {
            log.info("{} already indexed", url);
            return "EXIST INDEX";
        }

        log.info("{} task offer", url);
        lockMap.computeIfAbsent(url, e -> new ReentrantLock());
        CompletableFuture.runAsync(() -> {
            ReentrantLock reentrantLock = lockMap.computeIfAbsent(url, e -> new ReentrantLock());
            if (reentrantLock.tryLock()) {
                try {
                    List<Bookmark> doubleCheckBookmarks = indexEngine.termSearch(url, LambdaUtils.name(Bookmark::getUrl));
                    if (!CollectionUtils.isEmpty(doubleCheckBookmarks)) {
                        log.info("{} already indexed", url);
                        return;
                    }

                    log.debug("{} snapshotting...", url);
                    snapshotService.snapshot(url);
                    log.info("{} snapshot end", url);

                    Completion completion = completionService.completions(CompletionRequestParam.builder()
                            .model("silent_search")
                            .messages(List.of(CompletionRequestParam.CompletionMessage.builder()
                                    .role("user")
                                    .content(String.format(ApplicationConstants.KIMI_COMPLETION_PROMPT, url))
                                    .build()))
                            .use_search(true)
                            .stream(false)
                            .build());
                    log.info("{} completion result {}", url, completion.toString());
                    String aiSummary = Optional.of(completion)
                            .map(Completion::getChoices)
                            .map(List::getFirst)
                            .map(Completion.ChoiceOfCompletion::getMessage)
                            .map(Completion.MessageOfChoice::getContent).orElse(ApplicationConstants.DEFAULT_EXCEPTION_DESC);

                    log.debug("{} extracting content", url);
                    String content = extractService.extract(url, "");
                    log.info("{} extract end", url);

                    indexEngine.addDocument(Bookmark.builder()
                            .url(url)
                            .snapshotUrl(url)
                            .aiSummary(aiSummary)
                            .content(content)
                            .aiTagList(List.of())
                            .build());
                    log.info("{} task end", url);
                } finally {
                    if (reentrantLock.isLocked()) {
                        reentrantLock.unlock();
                    }
                    lockMap.remove(url);
                }
            }
        }, executor).exceptionally((e) -> {
            log.error("{} task error", url, e);
            return null;
        });
        return "OK";
    }

    @GetMapping("/checkTask")
    public String checkTask(@RequestParam String url) {
        if (Objects.nonNull(lockMap.get(url))) {
            log.info("{} already in task list", url);
            return "EXIST TASK";
        }

        // 是否已经解析过的url
        List<Bookmark> bookmarks = indexEngine.termSearch(url, LambdaUtils.name(Bookmark::getUrl));
        if (!CollectionUtils.isEmpty(bookmarks)) {
            log.info("{} already indexed", url);
            return "EXIST INDEX";
        }

        return "OK";
    }

    @GetMapping("/search")
    public Page<Bookmark> search(@RequestParam String keyword, @RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        ApplicationConstants.pageNumberAndPageSizeThreadLocal.set(Map.entry(pageNum, pageSize));

        List<Bookmark> termSearch = indexEngine.termSearch(keyword, LambdaUtils.name(Bookmark::getUrl));
        if (!CollectionUtils.isEmpty(termSearch)) {
            termSearch.forEach(e -> {
                Map<String, String> highlight = new HashMap<>();
                highlight.put(LambdaUtils.name(Bookmark::getUrl), "<b>" + keyword + "</b>");
                if (!StringUtils.isEmpty(e.getAiSummary())) {
                    highlight.put(LambdaUtils.name(Bookmark::getAiSummary), e.getAiSummary());
                }
                e.setHighlight(highlight);
            });

            return Page.of(pageNum, pageSize, termSearch.size(), termSearch);
        }
        return indexEngine.search(keyword, LambdaUtils.name(Bookmark::getAiSummary), LambdaUtils.name(Bookmark::getContent));
    }
}
