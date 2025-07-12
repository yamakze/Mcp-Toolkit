package com.wukoba.bilibili;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wukoba.bilibili.gateway.BiliBiliApiService;
import com.wukoba.bilibili.tools.BilibiliTools;
import com.wukoba.bilibili.utils.CookieLoader;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ToolCallbackProvider bilibiliToolsCallback(BilibiliTools bilibiliTools) {
        return MethodToolCallbackProvider.builder().toolObjects(bilibiliTools).build();
    }


    @Bean
    public BiliBiliApiService biliBiliApiService(CookieLoader cookieLoader) throws IOException {
        Map<String, String> cookies = cookieLoader.getCookies();
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request originalRequest = chain.request();
                    Request request = originalRequest.newBuilder()
                            .header("Cookie", cookies.entrySet().stream()
                                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                                    .collect(Collectors.joining("; ")))
                            .build();
                    return chain.proceed(request);
                })
                .build();

        String BASE_URL = "https://api.bilibili.com/";
        // 配置带有驼峰命名策略的 Gson
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(BiliBiliApiService.class);
    }
}
