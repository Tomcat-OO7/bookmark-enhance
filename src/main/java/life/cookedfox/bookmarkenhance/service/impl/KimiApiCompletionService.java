package life.cookedfox.bookmarkenhance.service.impl;

import jakarta.annotation.Resource;
import life.cookedfox.bookmarkenhance.service.ICompletionService;
import life.cookedfox.bookmarkenhance.model.Completion;
import life.cookedfox.bookmarkenhance.model.CompletionRequestParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class KimiApiCompletionService implements ICompletionService {

    @Resource
    RestClient restClient;

    @Value("${completions.kimi.uri}")
    String uri;

    @Value("${completions.kimi.authorization}")
    String authorization;

    @Override
    public Completion completions(CompletionRequestParam param) {
        return restClient.post()
                .uri(uri)
                .header("authorization", authorization)
                .body(param)
                .retrieve()
                .body(Completion.class);
    }
}
