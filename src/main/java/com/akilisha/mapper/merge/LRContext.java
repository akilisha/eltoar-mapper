package com.akilisha.mapper.merge;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class LRContext extends TreeMap<Integer, Map<String, List<String>>> {

    public void trace(Object parent, Object child, String field) {
        int parentHash = System.identityHashCode(parent);
        String childName = child.getClass().getName();
        log.debug("\nhash - {}\nparent - {}\nchild - {}, field - {}\n", parentHash, parent.getClass().getName(), childName, field);
        Map<String, List<String>> children = computeIfAbsent(parentHash, hash -> new LinkedHashMap<>());

        List<String> fieldNames;
        if (children.containsKey(childName)) {
            fieldNames = children.get(childName);
            if (fieldNames.contains(field)) {
                String error = String.format("Detected cycle from parent '%s' to child '%s' on the field '%s'",
                        parent.getClass().getName(), child.getClass().getName(), field);
                throw new RuntimeException(error);
            }
        } else {
            fieldNames = children.computeIfAbsent(childName, list -> new ArrayList<>());
        }
        fieldNames.add(field);
    }
}
