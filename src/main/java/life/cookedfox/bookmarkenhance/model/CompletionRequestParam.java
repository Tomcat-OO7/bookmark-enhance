package life.cookedfox.bookmarkenhance.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompletionRequestParam {
    private String model;
    private List<CompletionMessage> messages;
    private Boolean use_search;
    private Boolean stream;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CompletionMessage {
        private String role;
        private String content;
    }
}



