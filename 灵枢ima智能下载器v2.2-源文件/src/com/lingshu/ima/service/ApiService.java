//版权声明
//本软件由作者:灵枢 开发，仅供学习交流使用
//严禁任何形式的非法破解、反编译、逆向工程等行为
//使用者应当遵守相关法律法规，尊重知识产权
//作者:灵枢 保留所有权利

package com.lingshu.ima.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingshu.ima.config.AppConfig;
import com.lingshu.ima.dto.ShareInfoRequest;
import com.lingshu.ima.dto.ShareInfoResponse;
import com.lingshu.ima.dto.helper.KnowledgeItem;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//版权声明
//本软件由作者:灵枢 开发，仅供学习交流使用
//严禁任何形式的非法破解、反编译、逆向工程等行为
//使用者应当遵守相关法律法规，尊重知识产权
//作者:灵枢 保留所有权利

public class ApiService {
    
    private final AppConfig config;
    private final ObjectMapper objectMapper;
    
    public ApiService() {
        this.config = AppConfig.getInstance();
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    //版权声明
    //本软件由作者:灵枢 开发，仅供学习交流使用
    //严禁任何形式的非法破解、反编译、逆向工程等行为
    //使用者应当遵守相关法律法规，尊重知识产权
    //作者:灵枢 保留所有权利
    
    public ShareInfoResponse getShareInfo(String shareId, int limit, String cursor, String folderId) throws Exception {
        ShareInfoRequest request = new ShareInfoRequest(shareId, cursor, limit, folderId);
        String jsonRequest = objectMapper.writeValueAsString(request);
        
        URL url = new URL(config.getApiUrl());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        try {
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestProperty("Pragma", "no-cache");
            connection.setDoOutput(true);
            connection.setConnectTimeout(config.getApiTimeout());
            connection.setReadTimeout(config.getApiTimeout());
            
            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] input = jsonRequest.getBytes("utf-8");
                outputStream.write(input, 0, input.length);
            }
            
            int responseCode = connection.getResponseCode();
            
            if (responseCode != 200) {
                String errorResponse = "";
                try (BufferedReader errorReader = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream(), "utf-8"))) {
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        errorResponse += line;
                    }
                }
                throw new Exception("HTTP响应错误: " + responseCode + " - " + errorResponse);
            }
            
            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
            
            String responseString = response.toString();
            
            ShareInfoResponse shareInfoResponse = objectMapper.readValue(responseString, ShareInfoResponse.class);
            
            if (shareInfoResponse.getCode() != 0) {
                throw new Exception("API错误: " + shareInfoResponse.getMsg());
            }
            
            return shareInfoResponse;
            
        } finally {
            connection.disconnect();
        }
    }
    
    //版权声明
    //本软件由作者:灵枢 开发，仅供学习交流使用
    //严禁任何形式的非法破解、反编译、逆向工程等行为
    //使用者应当遵守相关法律法规，尊重知识产权
    //作者:灵枢 保留所有权利
    
    public List<KnowledgeItem> fetchShareInfo(String shareId) throws Exception {
        List<KnowledgeItem> allItems = new ArrayList<>();
        fetchShareInfoRecursive(shareId, "", "", allItems, 0, "");
        return allItems;
    }
    
    private void fetchShareInfoRecursive(String shareId, String cursor, String folderId, List<KnowledgeItem> allItems, int depth, String folderPath) throws Exception {
        int limit = 50;
        int maxPages = 100;
        int pageCount = 0;
        String currentCursor = cursor;
        String currentFolderId = folderId;
        
        while (pageCount < maxPages) {
            ShareInfoResponse response = getShareInfo(shareId, limit, currentCursor, currentFolderId);
            
            if (response.getKnowledgeList() != null && !response.getKnowledgeList().isEmpty()) {
                for (KnowledgeItem item : response.getKnowledgeList()) {
                    if ("99".equals(item.getMediaType())) {
                        String subFolderId = item.getMediaId();
                        String subFolderName = item.getTitle();
                        String subFolderPath = folderPath.isEmpty() ? subFolderName : folderPath + "/" + subFolderName;
                        
                        item.setSourcePath(subFolderPath);
                        allItems.add(item);
                        
                        try {
                            fetchShareInfoRecursive(shareId, "", subFolderId, allItems, depth + 1, subFolderPath);
                        } catch (Exception e) {
                            System.err.println("递归获取文件夹失败: " + subFolderName + " - " + e.getMessage());
                        }
                    } else {
                        item.setSourcePath(folderPath);
                        allItems.add(item);
                    }
                }
            }
            
            if (response.isEnd() || response.getNextCursor() == null || response.getNextCursor().isEmpty()) {
                break;
            }
            
            currentCursor = response.getNextCursor();
            pageCount++;
        }
    }
    

    
    public String extractShareId(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        
        String trimmed = input.trim();
        
        if (trimmed.matches("^[a-fA-F0-9]{64}$")) {
            return trimmed;
        }
        
        String pattern = "shareId[=]([a-fA-F0-9]{64})";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(trimmed);
        
        if (m.find()) {
            return m.group(1);
        }
        
        return null;
    }
    
    //版权声明
    //本软件由作者:灵枢 开发，仅供学习交流使用
    //严禁任何形式的非法破解、反编译、逆向工程等行为
    //使用者应当遵守相关法律法规，尊重知识产权
    //作者:灵枢 保留所有权利
}
