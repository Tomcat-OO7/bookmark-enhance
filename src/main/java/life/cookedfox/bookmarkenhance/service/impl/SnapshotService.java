package life.cookedfox.bookmarkenhance.service.impl;

import life.cookedfox.bookmarkenhance.utils.SystemCommandUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class SnapshotService {

    @Value("${snapshot.cmd}")
    private String cmd;

    @Value("${snapshot.path}")
    private String snapshotPath;

    public void saveSingleFile(String url) {
        SystemCommandUtils.exec(cmd
                .replaceAll("\\$path", snapshotPath + "/" + Base64.encodeBase64String(url.getBytes(StandardCharsets.UTF_8)) + ".html")
                .replaceAll("\\$url", url));
    }
}
