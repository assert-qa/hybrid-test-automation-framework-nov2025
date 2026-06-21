package api.payloads;

import managers.ConfigManager;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class AuthPayloads {
    private AuthPayloads() {
        // Utility class
    }

    public static Map<String, Object> validLoginPayload() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("email", ConfigManager.getValidLoginEmail());
        payload.put("password", ConfigManager.getValidLoginPassword());
        return payload;
    }

    public static Map<String, Object> invalidLoginPayload() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("email", ConfigManager.getValidLoginEmail());
        payload.put("password", ConfigManager.getValidLoginPassword() + "_wrong");
        return payload;
    }

    public static Map<String, Object> missingFieldLoginPayload() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("email", ConfigManager.getValidLoginEmail());
        return payload;
    }

    public static Map<String, Object> unregisteredLoginPayload() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("email", "unregistered-" + UUID.randomUUID() + "@example.com");
        payload.put("password", ConfigManager.getValidLoginPassword());
        return payload;
    }
}
