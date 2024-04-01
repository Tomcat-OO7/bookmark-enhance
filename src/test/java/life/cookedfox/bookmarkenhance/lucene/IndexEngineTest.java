package life.cookedfox.bookmarkenhance.lucene;

import jakarta.annotation.Resource;
import life.cookedfox.bookmarkenhance.model.Bookmark;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest
class IndexEngineTest {

    @Resource
    IndexEngine indexEngine;

    @Test
    void addDocument() throws IOException {
        indexEngine.addDocument(Bookmark.builder()
                .url("https://blog.csdn.net/chepang1905/article/details/100719176")
                .aiSummary("该CSDN博客文章讨论了在使用mac连接到centos服务器时遇到的一个常见问题，即出现警告信息“-bash: warning: setlocale: LC_CTYPE: cannot change locale (UTF-8): No such file or directory”。这个问题的根源在于SSH连接时会传递环境变量，而当服务器上没有相应的环境配置时，就无法识别这些变量，导致错误发生。\\n具体来说，问题是由于mac系统上的LC_CTYPE环境变量设置为UTF-8，而centos服务器上缺少相应的配置来识别和支持UTF-8编码，从而引发了警告。\\n文章提供了解决这个问题的方法，即在centos服务器上编辑`/etc/locale.conf`文件，添加以下配置：\\n```cobol\\nLC_ALL=en_US.utf8\\nLC_CTYPE=en_US.utf8\\nLANG=en_US.utf8\\n```\\n通过这样的配置，可以确保服务器上的环境变量与mac系统上的设置相匹配，从而解决警告问题。在进行了上述更改并重新连接服务器后，警告信息应该不再出现。")
                .snapshotUrl("https://blog.csdn.net/chepang1905/article/details/100719176")
                .aiTagList(List.of())
                .build());
    }

    @Test
    void search() throws Exception {
//        System.out.println(indexEngine.search("文章change cannot"));
    }
}