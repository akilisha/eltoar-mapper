package com.akilisha.mapper.perf.data.source;

import java.util.List;

public class RefundPolicy {

    private boolean isRefundable;
    private int refundTimeInDays;
    private List<String> notes;

    public RefundPolicy() {
    }

    public RefundPolicy(boolean isRefundable, int refundTimeInDays, List<String> notes) {

        this.isRefundable = isRefundable;
        this.refundTimeInDays = refundTimeInDays;
        this.notes = notes;
    }

    public boolean isRefundable() {
        return isRefundable;
    }

    public void setRefundable(boolean refundable) {
        isRefundable = refundable;
    }

    public int getRefundTimeInDays() {
        return refundTimeInDays;
    }

    public void setRefundTimeInDays(int refundTimeInDays) {
        this.refundTimeInDays = refundTimeInDays;
    }

    public List<String> getNotes() {
        return notes;
    }

    public void setNotes(List<String> notes) {
        this.notes = notes;
    }
}
