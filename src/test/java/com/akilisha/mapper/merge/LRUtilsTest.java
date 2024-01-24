package com.akilisha.mapper.merge;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.akilisha.mapper.merge.LRUtils.unfoldProperties;
import static org.assertj.core.api.Assertions.assertThat;

public class LRUtilsTest {

    @Test
    void test_unfoldPropertiesLeft() {
        Map<String, Object> map = new ConcurrentHashMap<>();
        Map<String, String> name = new ConcurrentHashMap<>();
        name.put("first", "hassan");
        name.put("last", "juma");
        Map<String, String> home = new ConcurrentHashMap<>();
        home.put("city", "detroit");
        home.put("state", "MI");
        map.put("res.name", name);
        map.put("res.home", home);
        map.put("date", new Date());

        Map<String, Object> dest = unfoldProperties(map);
        assertThat((Map<String, ?>) dest.get("res")).hasSize(2);
        assertThat(((Map<String, ?>) dest.get("res")).get("name")).isNotNull();
    }

    @Test
    void test_unfoldPropertiesRight() {
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("res.name.first", "hassan");
        map.put("res.name.last", "juma");
        map.put("res.home.city", "detroit");
        map.put("res.home.state", "MI");
        map.put("res.date", new Date());

        Map<String, Object> dest = unfoldProperties(map);
        assertThat((Map<String, ?>) dest.get("res")).hasSize(3);
        assertThat(((Map<String, ?>) dest.get("res")).get("name")).isNotNull();
    }
}
