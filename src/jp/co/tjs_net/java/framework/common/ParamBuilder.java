package jp.co.tjs_net.java.framework.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ParamBuilder<K, V> {
    private final Map<K, V> map;
    public ParamBuilder() {
        map = new HashMap<>();
    }
    public ParamBuilder<K, V> put(K key, V value) {
        map.put(key, value);
        return this;
    }
    public Map<K, V> toMap() {
        return map;
    }
    public Map<K, V> toConst() {
        return Collections.unmodifiableMap(map);
    }
}
