package com.akilisha.mapper.merge;

import lombok.*;

import java.util.Comparator;
import java.util.Objects;
import java.util.TreeSet;

public class LRContext extends TreeSet<LRContext.LREdge> {
    public LRContext() {
        super(new LREdgeComparator());
    }

    public void trace(Object parent, Object child, String field) {
        int parentHash = System.identityHashCode(parent);
        System.out.printf("hash - %d, class - %s, child - %s, field - %s\n", parentHash, parent.getClass().getName(), child.getClass().getName(), field);
        LREdge edge = new LREdge(parentHash, parent.getClass(), child.getClass(), field);

        if (contains(edge)) {
            String error = String.format("Detected cycle from parent '%s' to child '%s' on the field '%s'",
                    parent.getClass().getName(), child.getClass().getName(), field);
            throw new RuntimeException(error);

        } else {
            add(edge);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class LREdge {

        int hash;
        Class<?> parent;
        Class<?> child;
        String field;
    }

    public static class LREdgeComparator implements Comparator<LREdge> {

        @Override
        public int compare(LREdge o1, LREdge o2) {
            if (o1 == o2) return 0;

            if (o1.hash < o2.hash) return -1;
            if (o1.hash > o2.hash) return 1;

            int parentCompare = Objects.compare(o1.parent, o2.parent, Comparator.comparing((Class<?> x) -> x.toString()));
            if (parentCompare != 0) return parentCompare;

            int childCompare = Objects.compare(o1.parent, o2.parent, Comparator.comparing((Class<?> x) -> x.toString()));
            if (childCompare != 0) return childCompare;

            return Objects.requireNonNullElse(o1.field, "").compareTo(Objects.requireNonNullElse(o2.field, ""));
        }
    }
}
