package com.wukoba.bilibili.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CookieLoader {
    private final Map<String, String> cookies;

    public CookieLoader(@Value("${bilibili.api.cookie-file-path}") String cookiePath,
                        @Value("${bilibili.api.cookie-value}") String cookieValue) throws IOException {
        if (isNotBlank(cookiePath)) {
            this.cookies = loadCookiesByPath(cookiePath);
        } else if (isNotBlank(cookieValue)) {
            this.cookies = loadCookiesByCookieValue(cookieValue);
        } else {
            throw new RuntimeException("Cookies are not configured. Please check the properties: cookiePath and cookieValue");
        }
    }


    public Map<String, String> getCookies() {
        return cookies;
    }

    /**
     * 将cookie字符串解析为Map。
     *
     * @param cookieString cookie字符串
     * @return 包含键值对的Map
     */
    private Map<String, String> loadCookiesByCookieValue(String cookieString) {
        Map<String, String> cookies = new HashMap<>();

        if (cookieString == null || cookieString.isEmpty()) {
            return cookies;
        }

        // 按分号分割cookie字符串
        String[] cookiePairs = cookieString.split(";");

        for (String pair : cookiePairs) {
            // 去除空格并按等号分割键值对
            String[] keyValue = pair.trim().split("=", 2);

            if (keyValue.length == 2) {
                cookies.put(keyValue[0], keyValue[1]);
            }
        }

        return cookies;
    }

    private Map<String, String> loadCookiesByPath(String cookiePath) throws IOException {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(cookiePath)) {
            List<Cookie> cookies = gson.fromJson(reader, new TypeToken<List<Cookie>>() {
            }.getType());
            return cookies.stream().collect(Collectors.toMap(Cookie::name, Cookie::value));
        }
    }

    private boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }

}