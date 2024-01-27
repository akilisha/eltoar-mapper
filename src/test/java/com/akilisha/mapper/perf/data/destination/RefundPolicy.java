package com.akilisha.mapper.perf.data.destination;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (o.getClass() == RefundPolicy.class) {
            RefundPolicy that = (RefundPolicy) o;
            return isRefundable == that.isRefundable() &&
                    refundTimeInDays == that.getRefundTimeInDays() &&
                    Objects.equals(notes, that.getNotes());
        }
        if (o.getClass() != getClass()) return false;
        RefundPolicy that = (RefundPolicy) o;
        return isRefundable == that.isRefundable &&
                refundTimeInDays == that.refundTimeInDays &&
                Objects.equals(notes, that.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isRefundable, refundTimeInDays, notes);
    }
}
