package life.cookedfox.bookmarkenhance.service.impl;

import io.micrometer.observation.Observation;
import jakarta.annotation.Resource;
import life.cookedfox.bookmarkenhance.model.Bookmark;
import life.cookedfox.bookmarkenhance.service.ICompletionService;
import life.cookedfox.bookmarkenhance.model.CompletionRequestParam;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class KimiApiCompletionServiceTest {

    @Resource
    ICompletionService completionService;

    @Test
    public void test() {
        CompletionRequestParam build = CompletionRequestParam.builder()
                .model("silent_search")
                .messages(List.of(CompletionRequestParam.CompletionMessage.builder()
                        .role("user")
                        .content("总结https://blog.csdn.net/chepang1905/article/details/100719176")
                        .build()))
                .use_search(true)
                .stream(false)
                .build();
        System.out.println(completionService.completions(build));
    }
}