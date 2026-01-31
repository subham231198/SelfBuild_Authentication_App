package com.Banking.SelfBuild.Self.Build.Service;

import com.Banking.SelfBuild.Self.Build.POJO.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SessionService
{
    @Autowired
    private SessionManager sessionManager;

    public List<Map<String, Object>> getAllSessions()
    {
        return sessionManager.getList();
    }
}
