package software.renato.timemanager.common;

import java.util.Arrays;
import java.util.Objects;

public class ValidationUtil {

    public static boolean isAnyNull(Object... objs) {
        return Arrays.stream(objs).anyMatch(Objects::isNull);
    }

}
