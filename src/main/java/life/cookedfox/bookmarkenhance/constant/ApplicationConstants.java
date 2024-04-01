package life.cookedfox.bookmarkenhance.constant;

import java.util.Map;

public class ApplicationConstants {

    public static final String KIMI_COMPLETION_PROMPT = "作为专业摘要者，对所提供的网址（无论是文章、帖子、对话还是段落）创建简洁而全面的摘要，同时遵循以下准则：1.撰写详细、彻底、深入和复杂的摘要，同时保持清晰和简洁。2.纳入主要思想和基本信息，消除无关语言并关注关键方面 3. 将摘要格式化为段落形式以便于理解。总结我给你的这个网址。我的结果应该是纯文本形式一行的内容，其间不包含换行，或者markdown中使用的符号。网址为 %s";

    public static final ThreadLocal<Map.Entry<Integer, Integer>> pageNumberAndPageSizeThreadLocal = ThreadLocal.withInitial(() -> Map.entry(1, 20));
}
