package com.Banking.SelfBuild.Self.Build.Utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader
{
    public static String getHost(String key, String fileName)
    {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/resources/ServiceProperties/"+fileName+".properties"));
            String value = properties.getProperty(key);
            if(value != null)
            {
                return value;
            }
            else
            {
                throw new RuntimeException("Key not found in properties file: " + key);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getURL(String key, String fileName)
    {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/resources/ServiceProperties/"+fileName+".properties"));
            String value = properties.getProperty(key);
            if(value != null)
            {
                return value;
            }
            else
            {
                throw new RuntimeException("Key not found in properties file: " + key);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getAuthCredentials(String key)
    {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/resources/ServiceProperties/AuthCredentials.properties"));
            String value = properties.getProperty(key);
            if(value != null)
            {
                return value;
            }
            else
            {
                throw new RuntimeException("Key not found in properties file: " + key);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
