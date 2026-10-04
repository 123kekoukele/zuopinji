package com.hadluo.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class MajorConstants {

    private MajorConstants() {
    }

    public static final List<String> MAJOR_OPTIONS = Collections.unmodifiableList(Arrays.asList(
            "地理信息科学",
            "地理科学",
            "风景园林",
            "测绘工程",
            "城乡规划"
    ));

    public static String normalizeMajor(String major) {
        return major == null ? "" : major.trim();
    }

    public static boolean isMajorValid(String major) {
        String normalized = normalizeMajor(major);
        return !normalized.isEmpty() && MAJOR_OPTIONS.contains(normalized);
    }

    public static String toMajorCode(String major) {
        String normalized = normalizeMajor(major);
        if (!isMajorValid(normalized)) {
            return "";
        }
        int index = MAJOR_OPTIONS.indexOf(normalized);
        return index >= 0 ? "m" + (index + 1) : "";
    }
}

