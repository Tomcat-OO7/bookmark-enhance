package life.cookedfox.bookmarkenhance.service.impl;

import jakarta.annotation.Resource;
import life.cookedfox.bookmarkenhance.model.SnapshotResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@Slf4j
public class SnapshotService {

    @Resource
    RestClient restClient;

    @Value("${snapshot.uri}")
    String uri;

    public void snapshot(String url) {
        SnapshotResult res;
        try {
            res = restClient.post()
                    .uri(uri)
                    .body(Map.of("url", url))
                    .retrieve().body(SnapshotResult.class);
        } catch (Exception e) {
            log.error("{} snapshot异常", url, e);
            res = SnapshotResult.builder().code(500).build();
        }
        log.info("{} snapshot result {}", url, res);
    }
}
