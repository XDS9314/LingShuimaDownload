//版权声明
//本软件由作者:灵枢 开发，仅供学习交流使用
//严禁任何形式的非法破解、反编译、逆向工程等行为
//使用者应当遵守相关法律法规，尊重知识产权
//作者:灵枢 保留所有权利

package com.lingshu.ima.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

//版权声明
//本软件由作者:灵枢 开发，仅供学习交流使用
//严禁任何形式的非法破解、反编译、逆向工程等行为
//使用者应当遵守相关法律法规，尊重知识产权
//作者:灵枢 保留所有权利

public class AppConfig {
    
    private static final String CONFIG_FILE = "config/app.properties";
    private static AppConfig instance;
    private Properties properties;
    
    private AppConfig() {
        properties = new Properties();
        loadConfig();
    }
    
    //版权声明
    //本软件由作者:灵枢 开发，仅供学习交流使用
    //严禁任何形式的非法破解、反编译、逆向工程等行为
    //使用者应当遵守相关法律法规，尊重知识产权
    //作者:灵枢 保留所有权利
    
    public static synchronized AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }
    
    private void loadConfig() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                try (InputStreamReader fileReader = new InputStreamReader(new FileInputStream(CONFIG_FILE), StandardCharsets.UTF_8)) {
                    properties.load(fileReader);
                }
            } else {
                try (InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                    properties.load(reader);
                }
            }
        } catch (IOException e) {
            System.err.println("无法加载配置文件: " + e.getMessage());
            setDefaultConfig();
        }
    }
    
    private void setDefaultConfig() {
        properties.setProperty("app.name", "灵枢IMA知识库下载器V2.1");
        properties.setProperty("api.url", "https://ima.qq.com/cgi-bin/knowledge_share_get/get_share_info");
        properties.setProperty("api.limit.default", "20");
        properties.setProperty("download.thread", "100");
        properties.setProperty("download.dir", "./downloads");
        properties.setProperty("download.retry.count", "2");
        properties.setProperty("ui.progress.refresh", "1000");
        properties.setProperty("ui.list.auto.scroll", "true");
        properties.setProperty("download.fast.mode", "false");
    }
    
    public String getAppName() {
        return properties.getProperty("app.name");
    }
    
    public String getAppVersion() {
        return properties.getProperty("app.version", "2.0");
    }
    
    public String getApiUrl() {
        return properties.getProperty("api.url");
    }
    
    public int getApiLimitDefault() {
        return Integer.parseInt(properties.getProperty("api.limit.default", "20"));
    }
    
    public int getApiLimitMax() {
        return Integer.parseInt(properties.getProperty("api.limit.max", "50"));
    }
    
    public int getApiRetryCount() {
        return Integer.parseInt(properties.getProperty("api.retry.count", "3"));
    }
    
    public int getApiTimeout() {
        return Integer.parseInt(properties.getProperty("api.timeout", "30000"));
    }
    
    public int getDownloadThread() {
        return Integer.parseInt(properties.getProperty("download.thread", "20"));
    }
    
    public String getDownloadDir() {
        return properties.getProperty("download.dir", "./downloads");
    }
    
    public int getDownloadRetryCount() {
        return Integer.parseInt(properties.getProperty("download.retry.count", "2"));
    }
    
    public int getDownloadChunkSize() {
        return Integer.parseInt(properties.getProperty("download.chunk.size", "8192"));
    }
    
    public int getUiProgressRefresh() {
        return Integer.parseInt(properties.getProperty("ui.progress.refresh", "1000"));
    }
    
    public boolean isUiListAutoScroll() {
        return Boolean.parseBoolean(properties.getProperty("ui.list.auto.scroll", "true"));
    }
    
    public int getUiLogMaxLines() {
        return Integer.parseInt(properties.getProperty("ui.log.max.lines", "1000"));
    }
    
    public String getLogLevel() {
        return properties.getProperty("log.level", "INFO");
    }
    
    public String getLogFile() {
        return properties.getProperty("log.file", "./logs/app.log");
    }
    
    public boolean isFastDownloadMode() {
        return Boolean.parseBoolean(properties.getProperty("download.fast.mode", "false"));
    }
    
    public void setFastDownloadMode(boolean fastMode) {
        properties.setProperty("download.fast.mode", String.valueOf(fastMode));
    }
    
    //版权声明
    //本软件由作者:灵枢 开发，仅供学习交流使用
    //严禁任何形式的非法破解、反编译、逆向工程等行为
    //使用者应当遵守相关法律法规，尊重知识产权
    //作者:灵枢 保留所有权利
}
