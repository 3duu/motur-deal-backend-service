package br.com.motur.dealbackendservice.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.util.Map;

public class HeaderUtils {

    public static synchronized HttpEntity getHeader(String accessToken){
        HttpHeaders headers =  new HttpHeaders();
        headers.set("Authorization", accessToken);
        return new HttpEntity(headers);
    }

    public static HttpEntity getNotSyncHeader(final String accessToken){
        HttpHeaders headers =  new HttpHeaders();
        headers.set("Authorization", accessToken);
        return new HttpEntity(headers);
    }

    public static synchronized HttpEntity getHeaders(String accessToken, Map<String, String> headerMap){
        HttpHeaders headers =  new HttpHeaders();
        headers.set("Authorization", accessToken);
        for (Map.Entry<String, String> entry : headerMap.entrySet()){
            headers.set(entry.getKey(), entry.getValue());
        }

        return new HttpEntity(headers);
    }

    public static HttpEntity getHeaderAuthorizationBearer(final String auth) {
        HttpHeaders headers =  new HttpHeaders();
        if (auth.startsWith("Bearer ")) {
            headers.set("Authorization", auth);
        } else {
            headers.set("Authorization", "Bearer " + auth);
        }
        return new HttpEntity(headers);
    }

    public static HttpEntity getHeaderAuthorizationBearer(final String auth, Object body) {
        HttpHeaders headers =  new HttpHeaders();
        if (auth.startsWith("Bearer ")) {
            headers.set("Authorization", auth);
        } else {
            headers.set("Authorization", "Bearer " + auth);
        }
        return new HttpEntity(body, headers);
    }
}
