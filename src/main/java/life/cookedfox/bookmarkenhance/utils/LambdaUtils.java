package life.cookedfox.bookmarkenhance.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.function.Function;

@Slf4j
public class LambdaUtils {
    public static <T, R> String name(SFunction<T, R> func) {
        try {
            Method method = func.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(func);
            String fieldName = serializedLambda.getImplMethodName();
            if (fieldName.startsWith("get")) {
                fieldName = fieldName.substring(3);
            }
            if (fieldName.startsWith("is")) {
                fieldName = fieldName.substring(2);
            }
            return fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
        } catch (Exception e) {
            log.error("获取字段名异常", e);
            throw new RuntimeException(e);
        }
    }

    public interface SFunction<T, R> extends Function<T, R>, Serializable {
    }
}
