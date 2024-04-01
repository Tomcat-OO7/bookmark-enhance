package life.cookedfox.bookmarkenhance.service;

import life.cookedfox.bookmarkenhance.model.Completion;
import life.cookedfox.bookmarkenhance.model.CompletionRequestParam;

public interface ICompletionService {

    Completion completions(CompletionRequestParam param);
}
