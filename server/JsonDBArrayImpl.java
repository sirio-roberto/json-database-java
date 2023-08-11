package server;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class JsonDBArrayImpl implements JsonDBDao {

    private final Map<String, String> infoMap;

    public JsonDBArrayImpl() {
        infoMap = new HashMap<>();
    }

    @Override
    public String set(String key, String value) {
        infoMap.put(key, value);
        return "OK";
    }

    @Override
    public String get(String key) {
        if (infoMap.containsKey(key)) {
            return infoMap.get(key);
        }
        return "ERROR";
    }

    @Override
    public String delete(String key) {
        if (infoMap.containsKey(key)) {
            infoMap.remove(key);
            return "OK";
        }
        return "ERROR";
    }
}
