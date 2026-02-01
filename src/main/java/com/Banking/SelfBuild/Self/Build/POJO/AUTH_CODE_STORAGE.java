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
public class AUTH_CODE_STORAGE
{
    private List<Map<String, Object>> authCode_storage = new CopyOnWriteArrayList<>();;

    public List<Map<String, Object>> getAuthCode_storage() {
        return authCode_storage;
    }

    public void setAuthCode_storage(List<Map<String, Object>> authCode_storage) {
        this.authCode_storage = authCode_storage;
    }
}
