package life.cookedfox.bookmarkenhance.service.impl;

import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import life.cookedfox.bookmarkenhance.configuration.ApplicationPropertiesConfig;
import life.cookedfox.bookmarkenhance.constant.ApplicationConstants;
import life.cookedfox.bookmarkenhance.lucene.IndexEngine;
import life.cookedfox.bookmarkenhance.model.Bookmark;
import life.cookedfox.bookmarkenhance.model.Completion;
import life.cookedfox.bookmarkenhance.model.CompletionRequestParam;
import life.cookedfox.bookmarkenhance.model.Page;
import life.cookedfox.bookmarkenhance.service.ICompletionService;
import life.cookedfox.bookmarkenhance.service.IExtractService;
import life.cookedfox.bookmarkenhance.utils.LambdaUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class KimiApiCompletionService implements ICompletionService {

    @Resource
    RestClient restClient;

    @Resource
    ApplicationPropertiesConfig applicationPropertiesConfig;

    @Resource
    IndexEngine indexEngine;

    @Resource
    IExtractService extractService;

    @Resource
    SnapshotService snapshotService;

    @Override
    public Completion completions(CompletionRequestParam param) {
        Completion res;
        try {
            res = restClient.post()
                    .uri(applicationPropertiesConfig.getCompletionsKimiUri())
                    .header("authorization", applicationPropertiesConfig.getCompletionsKimiAuthorization())
                    .body(param)
                    .retrieve()
                    .body(Completion.class);
        } catch (Exception e) {
            log.error("{} 请求kimi api异常", param, e);
            res = Completion.builder().build();
        }
        return res;
    }

    @Scheduled(fixedDelay = 20, timeUnit = TimeUnit.MINUTES)
    public void rateLimitCompletions() {
        log.info("rateLimitCompletions scheduled");
        Page<Bookmark> bookmarksPage = indexEngine.search("\"" + ApplicationConstants.DEFAULT_EXCEPTION_DESC + "\""
                , LambdaUtils.name(Bookmark::getAiSummary));
        List<Bookmark> bookmarks = bookmarksPage.getContent();
        if (!CollectionUtils.isEmpty(bookmarks)) {
            Bookmark first = bookmarks.getFirst();
            log.info("retry url = {}", first.getUrl());
            Completion completion = completions(CompletionRequestParam.builder()
                    .model("silent_search")
                    .messages(List.of(CompletionRequestParam.CompletionMessage.builder()
                            .role("user")
                            .content(String.format(ApplicationConstants.KIMI_COMPLETION_PROMPT, first.getUrl()))
                            .build()))
                    .use_search(true)
                    .stream(false)
                    .build());
            log.info("{} completion result {}", first.getUrl(), completion.toString());
            String aiSummary = Optional.of(completion)
                    .map(Completion::getChoices)
                    .map(List::getFirst)
                    .map(Completion.ChoiceOfCompletion::getMessage)
                    .map(Completion.MessageOfChoice::getContent).orElse(ApplicationConstants.DEFAULT_EXCEPTION_DESC);
            first.setAiSummary(aiSummary);

            if (StringUtils.isBlank(first.getContent()) || ApplicationConstants.DEFAULT_EMPTY_CONTENT_DESC.equals(first.getContent())) {
                snapshotService.snapshot(first.getUrl());
                first.setContent(extractService.extract(first.getUrl(), ""));
            }
            indexEngine.updateDoc(first);
        }
    }
}