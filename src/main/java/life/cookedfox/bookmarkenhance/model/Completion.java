package life.cookedfox.bookmarkenhance.model;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Completion {
    private String id;
    private String model;
    private String objects;
    private List<ChoiceOfCompletion> choices;
    private UsageOfCompletion usage;
    private int created;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChoiceOfCompletion {
        private int index;
        private MessageOfChoice message;
        private String finish_reason;
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UsageOfCompletion {
        private int prompt_tokens;
        private int completion_tokens;
        private int total_tokens;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MessageOfChoice {
        private String role;
        private String content;
    }
}

