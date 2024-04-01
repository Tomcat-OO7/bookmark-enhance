package life.cookedfox.bookmarkenhance.model;


import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Bookmark {

    private String id;

    @NotEmpty
    private String url;

    private String highlight;

    private String aiSummary;

    private String content;

    private List<String> aiTagList;

    private String snapshotUrl;

    private LocalDateTime createTime;
}
