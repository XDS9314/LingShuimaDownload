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
import java.util.HashSet;
import java.util.Set;
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

    // V3.0 新增 - 通用POST请求方法

    public String postJson(String apiUrl, Object requestBody) throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(requestBody);

        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestProperty("Pragma", "no-cache");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            connection.setRequestProperty("Origin", "https://ima.qq.com");
            connection.setRequestProperty("Referer", "https://ima.qq.com/");
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
                throw new Exception("HTTP " + responseCode + " - " + errorResponse);
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            return response.toString();

        } finally {
            connection.disconnect();
        }
    }

    // V3.0 新增 - 搜索知识库

    public com.lingshu.ima.dto.SearchResponse searchKnowledgeBase(String query, int offset, int limit) throws Exception {
        com.lingshu.ima.dto.SearchRequest request = new com.lingshu.ima.dto.SearchRequest(query, offset, limit);
        String jsonStr = postJson(config.getApiSearchUrl(), request);
        com.lingshu.ima.dto.SearchResponse response = objectMapper.readValue(jsonStr, com.lingshu.ima.dto.SearchResponse.class);

        if (!response.isSuccess()) {
            throw new Exception("搜索API错误: " + response.getMsg());
        }

        return response;
    }

    // V3.0 新增 - 获取首页推荐

    public com.lingshu.ima.dto.HomepageResponse getHomepage(int offset, int limit) throws Exception {
        com.lingshu.ima.dto.HomepageRequest request = new com.lingshu.ima.dto.HomepageRequest(offset, limit);
        String jsonStr = postJson(config.getApiHomepageUrl(), request);
        com.lingshu.ima.dto.HomepageResponse response = objectMapper.readValue(jsonStr, com.lingshu.ima.dto.HomepageResponse.class);

        if (!response.isSuccess()) {
            throw new Exception("首页API错误: " + response.getMsg());
        }

        return response;
    }

    // V3.0 新增 - 获取知识库内容列表

    public com.lingshu.ima.dto.KnowledgeListResponse getKnowledgeList(String knowledgeBaseId, String parentFolderId, String cursor, int limit) throws Exception {
        com.lingshu.ima.dto.KnowledgeListRequest request = new com.lingshu.ima.dto.KnowledgeListRequest(knowledgeBaseId, parentFolderId, cursor, limit);
        String jsonStr = postJson(config.getApiKnowledgeListUrl(), request);
        com.lingshu.ima.dto.KnowledgeListResponse response = objectMapper.readValue(jsonStr, com.lingshu.ima.dto.KnowledgeListResponse.class);

        if (!response.isSuccess()) {
            throw new Exception("知识库列表API错误: " + response.getMsg());
        }

        return response;
    }

    // V3.0 新增 - 递归获取知识库全部内容

    public java.util.List<com.lingshu.ima.dto.helper.KnowledgeItem> fetchKnowledgeBaseAll(String knowledgeBaseId) throws Exception {
        java.util.List<com.lingshu.ima.dto.helper.KnowledgeItem> allItems = new java.util.ArrayList<>();
        java.util.Set<String> visitedFolders = new java.util.HashSet<>();
        fetchKnowledgeBaseRecursive(knowledgeBaseId, "", "", allItems, "", visitedFolders, 0);
        return allItems;
    }

    private void fetchKnowledgeBaseRecursive(String knowledgeBaseId, String parentFolderId, String cursor,
            java.util.List<com.lingshu.ima.dto.helper.KnowledgeItem> allItems, String folderPath,
            java.util.Set<String> visitedFolders, int depth) throws Exception {
        if (depth > 20) {
            System.err.println("递归深度超限(>20): " + folderPath);
            return;
        }
        int limit = config.getDownloadBatchFetchLimit();
        int maxPages = 100;
        int pageCount = 0;
        String currentCursor = cursor;

        while (pageCount < maxPages) {
            com.lingshu.ima.dto.KnowledgeListResponse response = getKnowledgeList(knowledgeBaseId, parentFolderId, currentCursor, limit);

            if (response.getKnowledge_list() != null && !response.getKnowledge_list().isEmpty()) {
                for (com.lingshu.ima.dto.helper.KnowledgeItem item : response.getKnowledge_list()) {
                    boolean isFolder = false;
                    if (item.getMediaId() != null && item.getMediaId().startsWith("folder_")) {
                        isFolder = true;
                    }
                    if (item.getMediaTypeInfo() != null && "文件夹".equals(item.getMediaTypeInfo().getName())) {
                        isFolder = true;
                    }

                    if (isFolder) {
                        String folderId = item.getMediaId();
                        // 防止循环引用导致的无限递归
                        if (visitedFolders.contains(folderId)) {
                            continue;
                        }
                        visitedFolders.add(folderId);

                        String subFolderPath = folderPath.isEmpty() ? item.getTitle() : folderPath + "/" + item.getTitle();
                        item.setSourcePath(subFolderPath);
                        allItems.add(item);

                        try {
                            fetchKnowledgeBaseRecursive(knowledgeBaseId, folderId, "", allItems, subFolderPath, visitedFolders, depth + 1);
                        } catch (Exception e) {
                            System.err.println("递归获取文件夹失败: " + item.getTitle() + " - " + e.getMessage());
                        }
                    } else {
                        item.setSourcePath(folderPath);
                        allItems.add(item);
                    }
                }
            }

            if (!response.isHas_more() || response.getNextCursor() == null || response.getNextCursor().isEmpty()) {
                break;
            }

            currentCursor = response.getNextCursor();
            pageCount++;
        }
    }

    //版权声明
//本软件由作者:灵枢 开发，仅供学习交流使用
//严禁任何形式的非法破解、反编译、逆向工程等行为
//使用者应当遵守相关法律法规，尊重知识产权
//作者:灵枢 保留所有权利
}
