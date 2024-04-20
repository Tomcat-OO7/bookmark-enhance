package life.cookedfox.bookmarkenhance.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
@Data
public class ApplicationPropertiesConfig {

    @Value("${lucene.index.path}")
    String luceneIndexPath;

    @Value("${completions.kimi.uri}")
    String completionsKimiUri;

    @Value("${completions.kimi.authorization}")
    String completionsKimiAuthorization;

    @Value("${extract.readability.uri}")
    String extractReadabilityUri;

    @Value("${snapshot.uri}")
    String snapshotUri;
}
