package api.payloads;

import managers.ConfigManager;

import java.util.LinkedHashMap;
import java.util.Map;

public class EventPayloads {
    // Parameter

    private EventPayloads() {
        // Utility class
    }

    public static Map<String, Object> getListEventsQueryParams(String category, String city, String search, String page, String limit){
        Map<String, Object> params = new LinkedHashMap<>();
        putIfPresent(params, "category", category);
        putIfPresent(params, "city", city);
        putIfPresent(params, "search", search);
        putIfPresent(params, "page", page);
        putIfPresent(params, "limit", limit);
        return params;
    }

    // Helper method
    public static void putIfPresent(Map<String, Object> params, String key, String value){
        if (value != null && !value.isBlank()
        && !"-".equals(value.trim())){
            params.put(key, value.trim());
        }
    }
}
