package life.cookedfox.bookmarkenhance.model;

import lombok.Data;

import java.util.List;

@Data
public class Page<T> {

    Integer pageNumber;
    Integer pageSize;
    Integer totalCount;
    List<T> content;

    public static <T> Page<T> of(Integer pageNumber, Integer pageSize, Integer totalCount, List<T> content) {
        Page<T> result = new Page<>();
        result.setPageSize(pageSize);
        result.setPageNumber(pageNumber);
        result.setTotalCount(totalCount);
        result.setContent(content);
        return result;
    }
}
