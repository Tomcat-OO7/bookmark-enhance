package life.cookedfox.bookmarkenhance.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Bookmark {

    private String id;

    private String url;

    private Map<String, String> highlight;

    private String aiSummary;

    private String content;

    private List<String> aiTagList;

    private String snapshotUrl;

    private LocalDateTime createTime;
}
