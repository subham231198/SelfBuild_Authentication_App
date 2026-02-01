package com.Banking.SelfBuild.Self.Build.POJO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class ACCESS_TOKEN_STORAGE
{
    private List<Map<String, Object>> accessToken_storage = new CopyOnWriteArrayList<>();;

    public List<Map<String, Object>> getAccessToken_storage() {
        return accessToken_storage;
    }

    public void setAccessToken_storage(List<Map<String, Object>> accessToken_storage) {
        this.accessToken_storage = accessToken_storage;
    }
}
