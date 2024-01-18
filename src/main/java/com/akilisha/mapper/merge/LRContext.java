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
        addHash(parent);
        get(parent).fields.add(new LRNode(parent, child, field));
    }

    public LRContext trace(Object parent, Object child, String field) {
        addEdge(System.identityHashCode(parent), child.getClass().getSimpleName(), field);
        return this;
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
        String className;
        String fieldName;
    }
}
