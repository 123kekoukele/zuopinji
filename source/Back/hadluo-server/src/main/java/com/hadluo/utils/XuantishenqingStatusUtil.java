package com.hadluo.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 选题申请审核状态常量与兼容判断
 */
public final class XuantishenqingStatusUtil {

    public static final String STATUS_PENDING = "未审核";
    public static final String STATUS_APPROVED = "通过";
    public static final String STATUS_REJECTED = "驳回";

    private static final List<String> APPLIED_STATUSES = Collections.unmodifiableList(
            Arrays.asList(STATUS_APPROVED, "已审核", "已通过", "是", "已申请")
    );
    private static final List<String> REJECTED_STATUSES = Collections.unmodifiableList(
            Arrays.asList(STATUS_REJECTED, "否", "已驳回")
    );
    private static final List<String> PENDING_STATUSES = Collections.unmodifiableList(
            Arrays.asList(STATUS_PENDING, "未申请", "待审核")
    );

    private XuantishenqingStatusUtil() {
    }

    public static List<String> getAppliedStatuses() {
        return APPLIED_STATUSES;
    }

    public static List<String> getRejectedStatuses() {
        return REJECTED_STATUSES;
    }

    public static List<String> getPendingStatuses() {
        return PENDING_STATUSES;
    }

    /** 仍占用选题名额的状态：未审核 + 通过 */
    public static List<String> getActiveStatuses() {
        List<String> active = new ArrayList<>();
        active.addAll(PENDING_STATUSES);
        active.addAll(APPLIED_STATUSES);
        return Collections.unmodifiableList(active);
    }

    public static boolean isActiveStatus(String status) {
        return isPendingStatus(status) || isAppliedStatus(status);
    }

    public static boolean isAppliedStatus(String status) {
        return status != null && APPLIED_STATUSES.contains(status.trim());
    }

    public static boolean isRejectedStatus(String status) {
        return status != null && REJECTED_STATUSES.contains(status.trim());
    }

    public static boolean isPendingStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return true;
        }
        String value = status.trim();
        return PENDING_STATUSES.contains(value)
                || (!isAppliedStatus(value) && !isRejectedStatus(value));
    }

    public static boolean isValidFilterStatus(String status) {
        return STATUS_PENDING.equals(status)
                || STATUS_APPROVED.equals(status)
                || STATUS_REJECTED.equals(status);
    }

    public static String normalizeDisplayStatus(String status) {
        if (isAppliedStatus(status)) {
            return STATUS_APPROVED;
        }
        if (isRejectedStatus(status)) {
            return STATUS_REJECTED;
        }
        return STATUS_PENDING;
    }
}
