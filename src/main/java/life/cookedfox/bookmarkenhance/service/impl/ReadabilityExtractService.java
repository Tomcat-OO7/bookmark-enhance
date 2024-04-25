package life.cookedfox.bookmarkenhance.service.impl;

import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import life.cookedfox.bookmarkenhance.configuration.ApplicationPropertiesConfig;
import life.cookedfox.bookmarkenhance.constant.ApplicationConstants;
import life.cookedfox.bookmarkenhance.model.ExtractResult;
import life.cookedfox.bookmarkenhance.service.IExtractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class ReadabilityExtractService implements IExtractService {

    @Resource
    RestClient restClient;

    @Resource
    ApplicationPropertiesConfig applicationPropertiesConfig;

    @Override
    public String extract(String url, String jsPath) {
        Map<String, String> body = new HashMap<>();
        body.put("url", url);
        if (StringUtils.isNotBlank(jsPath)) {
            body.put("jsPath", jsPath);
        }

        ExtractResult result;
        try {
            result = restClient.post()
                    .uri(applicationPropertiesConfig.getExtractReadabilityUri())
                    .body(body)
                    .retrieve()
                    .body(ExtractResult.class);
        } catch (Exception e) {
            result = ExtractResult.builder().build();
            log.error("{} 提取内容异常", url, e);
        }

        return Optional.ofNullable(result).map(ExtractResult::getContent).orElse(ApplicationConstants.DEFAULT_EMPTY_CONTENT_DESC);
    }
}
