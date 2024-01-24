package com.akilisha.mapper.merge;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class LRContext extends HashMap<Integer, LRContext.LREdge> {

    public void addHash(int hash) {
        computeIfAbsent(hash, LREdge::new);
    }

    public void addEdge(int parent, String child, String field) {
        if (field != null) {
            addHash(parent);
            get(parent).fields.add(new LRNode(parent, child, field));
        }
    }

    public void trace(Object parent, Object child, String field) {
        int parentHash = System.identityHashCode(parent);
        if (containsKey(parentHash)) {
            String error = get(parentHash).getFields().stream().filter(f -> f.getParent() == parentHash)
                    .findFirst().map(f -> String.format("Detected cycle from parent '%s' to child '%s' on the field '%s'",
                            parent.getClass().getSimpleName(), f.getChild(), f.getField())).orElse(
                            String.format("Detected cycle - trying to map %s again before the original mapping ic completed",
                                    parent
                                            .getClass().getSimpleName()));
            throw new RuntimeException(error);
        }
        //ok, looking good
        addEdge(parentHash, child.getClass().getName(), field);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LREdge {

        final List<LRNode> fields = new LinkedList<>();
        int parent;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LRNode {

        int parent;
        String child;
        String field;
    }
}
