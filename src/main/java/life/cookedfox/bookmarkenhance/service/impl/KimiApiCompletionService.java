package life.cookedfox.bookmarkenhance.service.impl;

import jakarta.annotation.Resource;
import life.cookedfox.bookmarkenhance.configuration.ApplicationPropertiesConfig;
import life.cookedfox.bookmarkenhance.model.Completion;
import life.cookedfox.bookmarkenhance.model.CompletionRequestParam;
import life.cookedfox.bookmarkenhance.service.ICompletionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class KimiApiCompletionService implements ICompletionService {

    @Resource
    RestClient restClient;

    @Resource
    ApplicationPropertiesConfig applicationPropertiesConfig;

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
}
