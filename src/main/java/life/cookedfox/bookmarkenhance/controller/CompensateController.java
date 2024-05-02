package life.cookedfox.bookmarkenhance.controller;

import jakarta.annotation.Resource;
import life.cookedfox.bookmarkenhance.lucene.IndexEngine;
import life.cookedfox.bookmarkenhance.model.Bookmark;
import life.cookedfox.bookmarkenhance.service.IExtractService;
import life.cookedfox.bookmarkenhance.utils.LambdaUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bookmark/compensate")
public class CompensateController {

    @Resource
    IExtractService extractService;

    @Resource
    IndexEngine indexEngine;

    @Value("${snapshot.path}")
    String snapshotPath;

    @SneakyThrows
    @PostMapping("/manualUploadSnapshot")
    public String manualUploadSnapshot(@RequestParam MultipartFile file, @RequestParam String url) {
        url = url.trim();
        if (!StringUtils.hasText(url)) {
            return "URL IS NOT BLANK";
        }

        List<Bookmark> bookmarks = indexEngine.termSearch(url, LambdaUtils.name(Bookmark::getUrl));
        if (CollectionUtils.isEmpty(bookmarks)) {
            return "URL IS NOT INDEXED";
        }

        String filename = Base64.getUrlEncoder().encodeToString(url.getBytes(Charset.forName("utf-8")));
        FileCopyUtils.copy(file.getBytes(), new File(snapshotPath + File.separator + filename + ".html"));

        Bookmark first = bookmarks.getFirst();
        first.setContent(extractService.extract(url, null));
        indexEngine.updateDoc(first);
        return "OK";
    }
}
