//版权声明
//本软件由作者:灵枢 开发，仅供学习交流使用
//严禁任何形式的非法破解、反编译、逆向工程等行为
//使用者应当遵守相关法律法规，尊重知识产权
//作者:灵枢 保留所有权利

package com.lingshu.ima.service;

import com.lingshu.ima.config.AppConfig;
import com.lingshu.ima.dto.ShareInfoRequest;
import com.lingshu.ima.dto.ShareInfoResponse;
import com.lingshu.ima.dto.helper.KnowledgeItem;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

//版权声明
//本软件由作者:灵枢 开发，仅供学习交流使用
//严禁任何形式的非法破解、反编译、逆向工程等行为
//使用者应当遵守相关法律法规，尊重知识产权
//作者:灵枢 保留所有权利

public class DownloadService {
    
    private final AppConfig config;
    private final ApiService apiService;
    private ExecutorService executorService;
    private final AtomicInteger completedCount;
    private final AtomicInteger failedCount;
    private final AtomicInteger existingCount;
    private final List<DownloadProgress> downloadList;
    private final Set<String> downloadedFiles;
    private boolean isRunning;
    
    public ApiService getApiService() {
        return apiService;
    }
    
    public boolean isRunning() {
        return isRunning;
    }
    
    public interface DownloadProgressCallback {
        void onProgress(DownloadProgress progress);
        void onComplete(int total, int success, int failed, int existing, long duration);
        void onError(String message);
    }
    
    public DownloadService() {
        this.config = AppConfig.getInstance();
        this.apiService = new ApiService();
        this.executorService = Executors.newFixedThreadPool(config.getDownloadThread());
        this.completedCount = new AtomicInteger(0);
        this.failedCount = new AtomicInteger(0);
        this.existingCount = new AtomicInteger(0);
        this.downloadList = new ArrayList<>();
        this.downloadedFiles = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.isRunning = false;
    }
    
    //版权声明
    //本软件由作者:灵枢 开发，仅供学习交流使用
    //严禁任何形式的非法破解、反编译、逆向工程等行为
    //使用者应当遵守相关法律法规，尊重知识产权
    //作者:灵枢 保留所有权利
    
    public void downloadAll(String shareId, DownloadProgressCallback callback) {
        downloadAll(java.util.Collections.singletonList(shareId), callback);
    }
    
    public void downloadAll(java.util.List<String> shareIds, DownloadProgressCallback callback) {
        downloadAll(shareIds, null, callback);
    }
    
    public void downloadAll(java.util.List<String> shareIds, java.util.List<KnowledgeItem> customItems, DownloadProgressCallback callback) {
        if (isRunning) {
            callback.onError("下载任务正在进行中");
            return;
        }
        
        ensureExecutorService();
        
        isRunning = true;
        completedCount.set(0);
        failedCount.set(0);
        existingCount.set(0);
        downloadList.clear();
        downloadedFiles.clear();
        downloadedFiles.clear();
        
        new Thread(() -> {
            long startTime = System.currentTimeMillis();
            
            try {
                java.util.List<KnowledgeItem> allItems = new java.util.ArrayList<>();
                java.util.Map<String, String> shareIdToFolderName = new java.util.HashMap<>();
                
                if (customItems != null && !customItems.isEmpty()) {
                    allItems.addAll(customItems);
                    
                    for (String currentShareId : shareIds) {
                        String folderName = getKnowledgeBaseName(currentShareId);
                        shareIdToFolderName.put(currentShareId, folderName);
                    }
                } else if (customItems == null) {
                    for (String currentShareId : shareIds) {
                        String folderName = getKnowledgeBaseName(currentShareId);
                        shareIdToFolderName.put(currentShareId, folderName);
                        
                        List<KnowledgeItem> items = apiService.fetchShareInfo(currentShareId);
                        allItems.addAll(items);
                    }
                } else {
                    for (String currentShareId : shareIds) {
                        if (!"pasted".equals(currentShareId)) {
                            String folderName = getKnowledgeBaseName(currentShareId);
                            shareIdToFolderName.put(currentShareId, folderName);
                            
                            List<KnowledgeItem> items = apiService.fetchShareInfo(currentShareId);
                            allItems.addAll(items);
                        } else {
                            shareIdToFolderName.put(currentShareId, "粘贴下载");
                        }
                    }
                }
                
                if (allItems.isEmpty()) {
                    callback.onError("未找到任何文件");
                    isRunning = false;
                    return;
                }
                
                callback.onProgress(new DownloadProgress("", allItems.size() + " 个文件", 0L, 0L, 0, 0L, 0L, ""));
                addLog("开始下载 " + allItems.size() + " 个文件，使用自动调整的并发线程");
                
                int submittedCount = 0;
                for (KnowledgeItem item : allItems) {
                    if (!isRunning) break;
                    
                    final String itemShareId = findShareIdForItem(item, shareIds);
                    final String folderName = shareIdToFolderName.get(itemShareId);
                    
                    // 直接提交任务，不限制并发数量
                    executorService.submit(() -> {
                        try {
                            if (isRunning) {
                                downloadItem(itemShareId, folderName, item, callback);
                            }
                        } catch (Exception e) {
                            failedCount.incrementAndGet();
                            callback.onError("下载失败: " + item.getTitle() + " - " + e.getMessage());
                        }
                    });
                    
                    submittedCount++;
                    if (submittedCount % 100 == 0) {
                        addLog("已提交 " + submittedCount + "/" + allItems.size() + " 个下载任务");
                    }
                }
                
                while (isRunning && (completedCount.get() + failedCount.get() + existingCount.get()) < allItems.size()) {
                    Thread.sleep(100);
                }
                
                long duration = System.currentTimeMillis() - startTime;
                callback.onComplete(allItems.size(), completedCount.get(), failedCount.get(), existingCount.get(), duration);
                
            } catch (Exception e) {
                callback.onError("下载过程出错: " + e.getMessage());
            } finally {
                isRunning = false;
            }
        }).start();
    }
    
    //版权声明
    //本软件由作者:灵枢 开发，仅供学习交流使用
    //严禁任何形式的非法破解、反编译、逆向工程等行为
    //使用者应当遵守相关法律法规，尊重知识产权
    //作者:灵枢 保留所有权利
    
    private void downloadItem(String shareId, String folderName, KnowledgeItem item, DownloadProgressCallback callback) {
        if (!isRunning) {
            return;
        }
        
        String jumpUrl = item.getJumpUrl();
        String rawFileUrl = item.getRawFileUrl();
        
        String downloadUrl = rawFileUrl != null && !rawFileUrl.isEmpty() ? rawFileUrl : jumpUrl;
        downloadUrl = fixDownloadUrl(downloadUrl);
        
        addLog("下载URL: " + downloadUrl + " (rawFileUrl: " + rawFileUrl + ", jumpUrl: " + jumpUrl + ")");
        
        String originalFileName = item.getTitle();
        String fileName = sanitizeFileName(originalFileName);
        String fileType = getFileType(item);
        String permission = getPermission(item);
        
        if ("99".equals(item.getMediaType()) || "文件夹".equals(fileType)) {
            addLog("处理文件夹: " + fileName + " [" + getCurrentTime() + "]");
            downloadFolderContent(shareId, folderName, item, callback);
            return;
        }
        
        if (downloadUrl == null || downloadUrl.isEmpty()) {
            addLog("跳过无效链接: " + fileName + " [" + getCurrentTime() + "]");
            existingCount.incrementAndGet();
            return;
        }
        
        String downloadDir = config.getDownloadDir() + File.separator + folderName;
        Path dirPath = Paths.get(downloadDir);
        
        try {
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
        } catch (IOException e) {
            failedCount.incrementAndGet();
            callback.onError("创建目录失败: " + e.getMessage());
            return;
        }
        
        File targetFile = new File(downloadDir + File.separator + fileName);
        
        String fileKey = generateFileKey(item, targetFile);
        synchronized (downloadedFiles) {
            if (downloadedFiles.contains(fileKey)) {
                existingCount.incrementAndGet();
                DownloadProgress progress = new DownloadProgress(fileType, originalFileName, 0L, 0L, 0, 0L, 0L, permission);
                progress.setStatus("已存在");
                callback.onProgress(progress);
                return;
            }
        }
        
        if (targetFile.exists()) {
            existingCount.incrementAndGet();
            downloadedFiles.add(fileKey);
            DownloadProgress progress = new DownloadProgress(fileType, originalFileName, 0L, 0L, 0, 0L, 0L, permission);
            progress.setStatus("已存在");
            callback.onProgress(progress);
            completedCount.incrementAndGet();
            return;
        }
        
        int retryCount = 0;
        int maxRetries = config.getDownloadRetryCount();
        
        while (retryCount <= maxRetries && isRunning) {
            try {
                downloadFileWithOriginalName(downloadUrl, targetFile, item, callback, originalFileName);
                if (isRunning) {
                    completedCount.incrementAndGet();
                    downloadedFiles.add(fileKey);
                }
                return;
            } catch (Exception e) {
                if (!isRunning) {
                    return;
                }
                
                retryCount++;
                addLog("下载失败 (尝试 " + retryCount + "/" + (maxRetries + 1) + "): " + fileName + " - " + e.getMessage());
                
                if (retryCount > maxRetries) {
                    if (rawFileUrl != null && !rawFileUrl.isEmpty() && !rawFileUrl.equals(downloadUrl)) {
                        addLog("尝试使用备用URL下载: " + fileName);
                        try {
                            downloadFileWithOriginalName(rawFileUrl, targetFile, item, callback, originalFileName);
                            if (isRunning) {
                                completedCount.incrementAndGet();
                            }
                            addLog("备用URL下载成功: " + fileName);
                            return;
                        } catch (Exception e2) {
                            addLog("备用URL下载也失败: " + fileName + " - " + e2.getMessage());
                        }
                    }
                    
                    failedCount.incrementAndGet();
                    callback.onError("下载失败: " + originalFileName + " - " + e.getMessage());
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }
    }
    
    //版权声明
    //本软件由作者:灵枢 开发，仅供学习交流使用
    //严禁任何形式的非法破解、反编译、逆向工程等行为
    //使用者应当遵守相关法律法规，尊重知识产权
    //作者:灵枢 保留所有权利
    
    private void downloadFolderContent(String shareId, String baseFolderName, KnowledgeItem folderItem, DownloadProgressCallback callback) {
        try {
            String folderId = folderItem.getMediaId();
            String folderTitle = sanitizeFileName(folderItem.getTitle());
            
            addLog("开始下载文件夹内容: " + folderTitle + " (ID: " + folderId + ") [" + getCurrentTime() + "]");
            
            java.util.List<KnowledgeItem> folderItems = fetchAllFolderItemsRecursive(shareId, folderId, "");
            
            if (folderItems.isEmpty()) {
                addLog("文件夹为空: " + folderTitle + " [" + getCurrentTime() + "]");
                return;
            }
            
            int fileCount = 0;
            for (KnowledgeItem item : folderItems) {
                if (!"99".equals(item.getMediaType())) {
                    fileCount++;
                }
            }
            
            addLog("文件夹 " + folderTitle + " 包含 " + fileCount + " 个文件（不包括文件夹） [" + getCurrentTime() + "]");
            
            String downloadDir = config.getDownloadDir() + File.separator + baseFolderName + File.separator + folderTitle;
            Path dirPath = Paths.get(downloadDir);
            try {
                if (!Files.exists(dirPath)) {
                    Files.createDirectories(dirPath);
                }
            } catch (IOException e) {
                callback.onError("创建文件夹失败: " + e.getMessage());
                return;
            }
            
            for (KnowledgeItem item : folderItems) {
                if (!isRunning) break;
                
                String filePath = item.getSourcePath();
                String itemDownloadDir = downloadDir;
                
                if (filePath != null && !filePath.isEmpty()) {
                    String[] pathParts = filePath.split("/");
                    for (String part : pathParts) {
                        if (!part.isEmpty()) {
                            String safePart = sanitizeFileName(part);
                            itemDownloadDir = itemDownloadDir + File.separator + safePart;
                        }
                    }
                    
                    try {
                        Files.createDirectories(Paths.get(itemDownloadDir));
                    } catch (IOException e) {
                        addLog("创建子目录失败: " + itemDownloadDir + " - " + e.getMessage());
                    }
                }
                
                if ("99".equals(item.getMediaType()) || "文件夹".equals(getFileType(item))) {
                    String subFolderName = sanitizeFileName(item.getTitle());
                    String subFolderPath = itemDownloadDir + File.separator + subFolderName;
                    try {
                        Files.createDirectories(Paths.get(subFolderPath));
                        addLog("创建子文件夹: " + subFolderName);
                        completedCount.incrementAndGet();
                    } catch (IOException e) {
                        addLog("创建子文件夹失败: " + subFolderName + " - " + e.getMessage());
                    }
                } else {
                    downloadFileInFolder(shareId, itemDownloadDir, item, callback);
                }
            }
            
        } catch (Exception e) {
            failedCount.incrementAndGet();
            callback.onError("处理文件夹失败: " + folderItem.getTitle() + " - " + e.getMessage());
        }
    }
    
    public List<KnowledgeItem> fetchFolderItemsPublic(String shareId, String folderId) throws Exception {
        List<KnowledgeItem> allItems = new ArrayList<>();
        String cursor = "";
        int limit = 50;
        int maxPages = 100;
        int pageCount = 0;
        
        while (pageCount < maxPages) {
            ShareInfoResponse response = apiService.getShareInfo(shareId, limit, cursor, folderId);
            
            if (response.getKnowledgeList() != null && !response.getKnowledgeList().isEmpty()) {
                allItems.addAll(response.getKnowledgeList());
            }
            
            if (response.isEnd() || response.getNextCursor() == null || response.getNextCursor().isEmpty()) {
                break;
            }
            
            cursor = response.getNextCursor();
            pageCount++;
        }
        
        return allItems;
    }
    
    public List<KnowledgeItem> fetchAllFolderItemsRecursive(String shareId, String folderId, String folderPath) throws Exception {
        List<KnowledgeItem> allItems = new ArrayList<>();
        fetchFolderItemsRecursive(shareId, folderId, folderPath, allItems);
        return allItems;
    }
    
    private void fetchFolderItemsRecursive(String shareId, String folderId, String folderPath, List<KnowledgeItem> allItems) throws Exception {
        List<KnowledgeItem> currentItems = fetchFolderItemsPublic(shareId, folderId);
        
        addLog("递归获取文件夹: " + folderPath + " (ID: " + folderId + ")，获取到 " + currentItems.size() + " 个项目");
        
        for (KnowledgeItem item : currentItems) {
            if ("99".equals(item.getMediaType())) {
                String subFolderId = item.getMediaId();
                String subFolderPath = folderPath.isEmpty() ? item.getTitle() : folderPath + "/" + item.getTitle();
                addLog("发现子文件夹: " + item.getTitle() + " (ID: " + subFolderId + ")，递归获取内容...");
                
                try {
                    fetchFolderItemsRecursive(shareId, subFolderId, subFolderPath, allItems);
                } catch (Exception e) {
                    addLog("递归获取子文件夹失败: " + item.getTitle() + " - " + e.getMessage());
                }
            } else {
                item.setSourcePath(folderPath);
                allItems.add(item);
            }
        }
    }
    
    private void downloadFileInFolder(String shareId, String folderPath, KnowledgeItem item, DownloadProgressCallback callback) {
        String jumpUrl = item.getJumpUrl();
        jumpUrl = fixDownloadUrl(jumpUrl);
        
        String originalFileName = item.getTitle();
        String fileName = sanitizeFileName(originalFileName);
        String fileType = getFileType(item);
        String permission = getPermission(item);
        
        if (jumpUrl == null || jumpUrl.isEmpty()) {
            addLog("跳过无效链接: " + fileName + " [" + getCurrentTime() + "]");
            existingCount.incrementAndGet();
            return;
        }
        
        File targetFile = new File(folderPath + File.separator + fileName);
        
        String fileKey = generateFileKey(item, targetFile);
        synchronized (downloadedFiles) {
            if (downloadedFiles.contains(fileKey)) {
                existingCount.incrementAndGet();
                DownloadProgress progress = new DownloadProgress(fileType, originalFileName, 0L, 0L, 0, 0L, 0L, permission);
                progress.setStatus("已存在");
                callback.onProgress(progress);
                return;
            }
        }
        
        if (targetFile.exists()) {
            existingCount.incrementAndGet();
            downloadedFiles.add(fileKey);
            DownloadProgress progress = new DownloadProgress(fileType, originalFileName, 0L, 0L, 0, 0L, 0L, permission);
            progress.setStatus("已存在");
            callback.onProgress(progress);
            return;
        }
        
        int retryCount = 0;
        int maxRetries = config.getDownloadRetryCount();
        
        while (retryCount <= maxRetries) {
            try {
                downloadFileWithOriginalName(jumpUrl, targetFile, item, callback, originalFileName);
                completedCount.incrementAndGet();
                downloadedFiles.add(fileKey);
                return;
            } catch (Exception e) {
                retryCount++;
                if (retryCount > maxRetries) {
                    failedCount.incrementAndGet();
                    callback.onError("下载失败: " + originalFileName + " - " + e.getMessage());
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }
    
    private void addLog(String message) {
        // 日志输出已移除，避免控制台输出
    }
    
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }
    
    private String generateFileKey(KnowledgeItem item, File targetFile) {
        String mediaId = item.getMediaId();
        if (mediaId != null && !mediaId.isEmpty() && !"0".equals(mediaId)) {
            return "mediaId:" + mediaId;
        }
        
        String fileSize = item.getFileSize();
        if (fileSize != null && !fileSize.isEmpty() && !"0".equals(fileSize)) {
            return "path:" + targetFile.getAbsolutePath() + "_size:" + fileSize;
        }
        
        return "path:" + targetFile.getAbsolutePath();
    }
    
    private void downloadFile(String urlStr, File targetFile, KnowledgeItem item, DownloadProgressCallback callback) throws Exception {
        downloadFileWithOriginalName(urlStr, targetFile, item, callback, item.getTitle());
    }
    
    private void downloadFileWithOriginalName(String urlStr, File targetFile, KnowledgeItem item, DownloadProgressCallback callback, String originalFileName) throws Exception {
        String fileType = getFileType(item);
        String permission = getPermission(item);
        
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        long startTime = System.currentTimeMillis();
        
        try {
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(60000);
            connection.setUseCaches(false);
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            connection.setRequestProperty("Accept", "*/*");
            connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
            connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestProperty("Pragma", "no-cache");
            connection.setInstanceFollowRedirects(true);
            connection.setDoOutput(true);
            
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("HTTP响应错误: " + responseCode);
            }
            
            int fileSize = 0;
            try {
                String sizeStr = item.getFileSize();
                if (sizeStr != null && !sizeStr.isEmpty() && !"0".equals(sizeStr)) {
                    fileSize = Integer.parseInt(sizeStr);
                }
            } catch (NumberFormatException e) {
                fileSize = connection.getContentLength();
            }
            
            if (fileSize <= 0) {
                fileSize = connection.getContentLength();
            }
            
            String encoding = connection.getContentEncoding();
            java.io.InputStream inputStream;
            if ("gzip".equalsIgnoreCase(encoding)) {
                inputStream = new java.util.zip.GZIPInputStream(connection.getInputStream());
            } else if ("deflate".equalsIgnoreCase(encoding)) {
                inputStream = new java.util.zip.InflaterInputStream(connection.getInputStream());
            } else {
                inputStream = connection.getInputStream();
            }
            
            try (BufferedInputStream in = new BufferedInputStream(inputStream, 1048576);
                 BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(targetFile), 1048576)) {
                
                byte[] dataBuffer = new byte[1048576];
                int bytesRead;
                int totalBytesRead = 0;
                
                while ((bytesRead = in.read(dataBuffer, 0, dataBuffer.length)) != -1 && isRunning) {
                    out.write(dataBuffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                    
                    if (fileSize > 0 && totalBytesRead % 20 == 0) {
                        int progress = (int) ((totalBytesRead * 100) / fileSize);
                        long elapsedTime = System.currentTimeMillis() - startTime;
                        long speed = elapsedTime > 0 ? (totalBytesRead * 1000) / elapsedTime : 0;
                        
                        DownloadProgress dp = new DownloadProgress(fileType, originalFileName, fileSize, totalBytesRead, progress, speed, elapsedTime, permission);
                        dp.setStatus("下载中");
                        callback.onProgress(dp);
                    }
                }
                
                if (!isRunning) {
                    targetFile.delete();
                    throw new InterruptedException("下载已停止");
                }
            }
            
            DownloadProgress progress = new DownloadProgress(fileType, originalFileName, fileSize, fileSize, 100, 0L, System.currentTimeMillis() - startTime, permission);
            progress.setStatus("已完成");
            callback.onProgress(progress);
            
        } finally {
            connection.disconnect();
        }
    }
    
    private String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "unknown_file";
        }
        
        String sanitized = fileName;
        
        sanitized = sanitized.replaceAll("[\\\\/:*?\"<>|]", "_");
        sanitized = sanitized.replaceAll("\\s+", "_");
        sanitized = sanitized.replaceAll("[\\x00-\\x1F\\x7F]", "");
        
        sanitized = sanitized.replace("〔", "_");
        sanitized = sanitized.replace("〕", "_");
        sanitized = sanitized.replace("《", "_");
        sanitized = sanitized.replace("》", "_");
        
        if (sanitized.length() > 200) {
            String ext = getFileExtension(sanitized);
            String nameWithoutExt = sanitized.substring(0, sanitized.length() - ext.length());
            nameWithoutExt = nameWithoutExt.substring(0, Math.min(200, nameWithoutExt.length()));
            sanitized = nameWithoutExt + ext;
        }
        
        return sanitized;
    }
    
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0 && lastDot < fileName.length() - 1) {
            return fileName.substring(lastDot);
        }
        return "";
    }
    
    private String getFileType(KnowledgeItem item) {
        if (item.getMediaTypeInfo() != null && item.getMediaTypeInfo().getName() != null) {
            return item.getMediaTypeInfo().getName();
        }
        
        String mediaType = item.getMediaType();
        if (mediaType == null) {
            return "未知";
        }
        
        switch (mediaType) {
            case "1":
                return "图片";
            case "2":
                return "视频";
            case "3":
                return "文档";
            case "4":
                return "音频";
            case "5":
                return "表格";
            case "6":
                return "PPT";
            case "7":
                return "PDF";
            case "8":
                return "压缩包";
            case "9":
                return "图片";
            case "99":
                return "文件夹";
            default:
                return "其他";
        }
    }
    
    private String getPermission(KnowledgeItem item) {
        int accessStatus = item.getAccessStatus();
        switch (accessStatus) {
            case 1:
                return "可查看";
            case 2:
                return "可下载";
            case 3:
                return "完全访问";
            default:
                return "未知";
        }
    }
    
    private String findShareIdForItem(KnowledgeItem item, java.util.List<String> shareIds) {
        String jumpUrl = item.getJumpUrl();
        if (jumpUrl != null && !jumpUrl.isEmpty()) {
            String pattern = "shareId[=]([a-fA-F0-9]{64})";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(jumpUrl);
            
            if (m.find()) {
                String extractedShareId = m.group(1);
                for (String shareId : shareIds) {
                    if (shareId.equals(extractedShareId)) {
                        return shareId;
                    }
                }
            }
        }
        
        return shareIds.isEmpty() ? null : shareIds.get(0);
    }
    
    private String getKnowledgeBaseName(String shareId) {
        try {
            if (shareId == null || shareId.isEmpty()) {
                return "知识库";
            }
            
            String idSuffix = "";
            if (shareId.length() >= 6) {
                idSuffix = shareId.substring(shareId.length() - 6);
            } else {
                idSuffix = shareId;
            }
            
            String knowledgeBaseName = null;
            
            try {
                ShareInfoResponse response = apiService.getShareInfo(shareId, 1, "", "");
                if (response.getCurrentPath() != null && !response.getCurrentPath().isEmpty()) {
                    String folderName = response.getCurrentPath().get(0).getFolderName();
                    if (folderName != null && !folderName.isEmpty()) {
                        knowledgeBaseName = folderName;
                    }
                }
            } catch (Exception e) {
                System.err.println("获取知识库名称失败，使用默认名称: " + e.getMessage());
            }
            
            if (knowledgeBaseName != null && !knowledgeBaseName.isEmpty()) {
                String finalName = "知识库-" + knowledgeBaseName + "-" + idSuffix;
                return finalName;
            } else {
                return "知识库-" + idSuffix;
            }
            
        } catch (Exception e) {
            System.err.println("获取知识库名称失败: " + e.getMessage());
            return "知识库";
        }
    }
    
    public void stop() {
        isRunning = false;
        executorService.shutdownNow();
    }
    
    private void ensureExecutorService() {
        if (executorService.isShutdown() || executorService.isTerminated()) {
            executorService = Executors.newFixedThreadPool(config.getDownloadThread());
        }
    }
    
    public void downloadFastMode(java.util.List<String> shareIds, java.util.List<KnowledgeItem> allItems, DownloadProgressCallback callback) {
        if (isRunning) {
            callback.onError("下载任务正在进行中");
            return;
        }
        
        ensureExecutorService();
        
        isRunning = true;
        completedCount.set(0);
        failedCount.set(0);
        existingCount.set(0);
        downloadList.clear();
        
        new Thread(() -> {
            long startTime = System.currentTimeMillis();
            
            try {
                if (allItems.isEmpty()) {
                    callback.onError("未找到任何文件");
                    isRunning = false;
                    return;
                }
                
                java.util.Map<String, String> shareIdToFolderName = new java.util.HashMap<>();
                
                for (String shareId : shareIds) {
                    if (!"pasted".equals(shareId)) {
                        String folderName = getKnowledgeBaseName(shareId);
                        shareIdToFolderName.put(shareId, folderName);
                    } else {
                        shareIdToFolderName.put(shareId, "粘贴下载");
                    }
                }
                
                callback.onProgress(new DownloadProgress("", allItems.size() + " 个文件", 0L, 0L, 0, 0L, 0L, ""));
                addLog("快速模式开始下载 " + allItems.size() + " 个文件，直接使用下载链接");
                
                int submittedCount = 0;
                for (KnowledgeItem item : allItems) {
                    if (!isRunning) break;
                    
                    final String itemShareId = findShareIdForItem(item, shareIds);
                    final String folderName = shareIdToFolderName.get(itemShareId);
                    
                    executorService.submit(() -> {
                        try {
                            if (isRunning) {
                                downloadItemFastMode(itemShareId, folderName, item, callback);
                            }
                        } catch (Exception e) {
                            if (isRunning) {
                                failedCount.incrementAndGet();
                                callback.onError("下载失败: " + item.getTitle() + " - " + e.getMessage());
                            }
                        }
                    });
                    
                    submittedCount++;
                    if (submittedCount % 100 == 0) {
                        addLog("已提交 " + submittedCount + "/" + allItems.size() + " 个下载任务");
                    }
                }
                
                while (isRunning && (completedCount.get() + failedCount.get() + existingCount.get()) < allItems.size()) {
                    Thread.sleep(100);
                }
                
                long duration = System.currentTimeMillis() - startTime;
                callback.onComplete(allItems.size(), completedCount.get(), failedCount.get(), existingCount.get(), duration);
                
            } catch (Exception e) {
                callback.onError("下载过程出错: " + e.getMessage());
            } finally {
                isRunning = false;
            }
        }).start();
    }
    
    private void downloadItemFastMode(String shareId, String folderName, KnowledgeItem item, DownloadProgressCallback callback) {
        String jumpUrl = item.getJumpUrl();
        String rawFileUrl = item.getRawFileUrl();
        
        String downloadUrl = rawFileUrl != null && !rawFileUrl.isEmpty() ? rawFileUrl : jumpUrl;
        downloadUrl = fixDownloadUrl(downloadUrl);
        
        String originalFileName = item.getTitle();
        String fileName = sanitizeFileName(originalFileName);
        String fileType = getFileType(item);
        String permission = getPermission(item);
        
        if ("99".equals(item.getMediaType()) || "文件夹".equals(fileType)) {
            createFolderStructure(folderName, item);
            addLog("快速模式创建文件夹: " + fileName);
            completedCount.incrementAndGet();
            return;
        }
        
        if (downloadUrl == null || downloadUrl.isEmpty()) {
            addLog("快速模式跳过无效链接: " + fileName);
            existingCount.incrementAndGet();
            return;
        }
        
        String downloadDir = config.getDownloadDir() + File.separator + folderName;
        
        String sourcePath = item.getSourcePath();
        if (sourcePath != null && !sourcePath.isEmpty()) {
            String[] pathParts = sourcePath.split("/");
            for (String part : pathParts) {
                if (!part.isEmpty()) {
                    String safePart = sanitizeFileName(part);
                    downloadDir = downloadDir + File.separator + safePart;
                }
            }
        }
        
        Path dirPath = Paths.get(downloadDir);
        
        try {
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
        } catch (IOException e) {
            failedCount.incrementAndGet();
            callback.onError("创建目录失败: " + e.getMessage());
            return;
        }
        
        File targetFile = new File(downloadDir + File.separator + fileName);
        
        String fileKey = generateFileKey(item, targetFile);
        synchronized (downloadedFiles) {
            if (downloadedFiles.contains(fileKey)) {
                existingCount.incrementAndGet();
                DownloadProgress progress = new DownloadProgress(fileType, originalFileName, 0L, 0L, 0, 0L, 0L, permission);
                progress.setStatus("已存在");
                callback.onProgress(progress);
                return;
            }
        }
        
        if (targetFile.exists()) {
            existingCount.incrementAndGet();
            downloadedFiles.add(fileKey);
            DownloadProgress progress = new DownloadProgress(fileType, originalFileName, 0L, 0L, 0, 0L, 0L, permission);
            progress.setStatus("已存在");
            callback.onProgress(progress);
            return;
        }
        
        addLog("快速模式开始下载: " + fileName + "，路径: " + downloadDir + " [" + getCurrentTime() + "]");
        addLog("下载URL: " + downloadUrl + " [" + getCurrentTime() + "]");
        
        int retryCount = 0;
        int maxRetries = config.getDownloadRetryCount();
        
        while (retryCount <= maxRetries && isRunning) {
            try {
                downloadFileWithOriginalName(downloadUrl, targetFile, item, callback, originalFileName);
                if (isRunning) {
                    completedCount.incrementAndGet();
                    downloadedFiles.add(fileKey);
                }
                return;
            } catch (Exception e) {
                if (!isRunning) {
                    return;
                }
                
                retryCount++;
                addLog("下载失败 (尝试 " + retryCount + "/" + (maxRetries + 1) + "): " + fileName + " - " + e.getMessage());
                
                if (retryCount > maxRetries) {
                    if (rawFileUrl != null && !rawFileUrl.isEmpty() && !rawFileUrl.equals(downloadUrl)) {
                        addLog("尝试使用备用URL下载: " + fileName);
                        try {
                            downloadFileWithOriginalName(rawFileUrl, targetFile, item, callback, originalFileName);
                            if (isRunning) {
                                completedCount.incrementAndGet();
                            }
                            addLog("备用URL下载成功: " + fileName);
                            return;
                        } catch (Exception e2) {
                            addLog("备用URL下载也失败: " + fileName + " - " + e2.getMessage());
                        }
                    }
                    
                    failedCount.incrementAndGet();
                    String errorMsg = e.getMessage();
                    if (errorMsg == null || errorMsg.isEmpty()) {
                        errorMsg = e.getClass().getSimpleName();
                    }
                    callback.onError("下载失败: " + originalFileName + " - " + errorMsg);
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }
    }
    
    public List<KnowledgeItem> fetchShareInfo(String shareId) throws Exception {
        return apiService.fetchShareInfo(shareId);
    }
    
    public static class DownloadProgress {
        private String fileType;
        private String fileName;
        private long fileSize;
        private long downloadedBytes;
        private int progress;
        private long speed;
        private long duration;
        private String status;
        private String permission;
        
        public DownloadProgress(String fileType, String fileName, long fileSize, long downloadedBytes, int progress, long speed, long duration, String permission) {
            this.fileType = fileType;
            this.fileName = fileName;
            this.fileSize = fileSize;
            this.downloadedBytes = downloadedBytes;
            this.progress = progress;
            this.speed = speed;
            this.duration = duration;
            this.status = "等待中";
            this.permission = permission;
        }
        
        public String getFileType() {
            return fileType;
        }
        
        public void setFileType(String fileType) {
            this.fileType = fileType;
        }
        
        public String getFileName() {
            return fileName;
        }
        
        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
        
        public long getFileSize() {
            return fileSize;
        }
        
        public void setFileSize(long fileSize) {
            this.fileSize = fileSize;
        }
        
        public long getDownloadedBytes() {
            return downloadedBytes;
        }
        
        public void setDownloadedBytes(long downloadedBytes) {
            this.downloadedBytes = downloadedBytes;
        }
        
        public int getProgress() {
            return progress;
        }
        
        public void setProgress(int progress) {
            this.progress = progress;
        }
        
        public long getSpeed() {
            return speed;
        }
        
        public void setSpeed(long speed) {
            this.speed = speed;
        }
        
        public long getDuration() {
            return duration;
        }
        
        public void setDuration(long duration) {
            this.duration = duration;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public String getPermission() {
            return permission;
        }
        
        public void setPermission(String permission) {
            this.permission = permission;
        }
    }
    
    private String fixDownloadUrl(String url) {
        if (url == null || url.isEmpty()) {
            return url;
        }
        
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }
        
        if (url.contains("ima.qq.com")) {
            return url;
        }
        
        if (url.startsWith("/")) {
            return "https://ima.qq.com" + url;
        }
        
        return "https://ima.qq.com/" + url;
    }
    
    private void createFolderStructure(String folderName, KnowledgeItem item) {
        String downloadDir = config.getDownloadDir() + File.separator + folderName;
        
        String sourcePath = item.getSourcePath();
        if (sourcePath != null && !sourcePath.isEmpty()) {
            String[] pathParts = sourcePath.split("/");
            for (String part : pathParts) {
                if (!part.isEmpty()) {
                    String safePart = sanitizeFileName(part);
                    downloadDir = downloadDir + File.separator + safePart;
                }
            }
        }
        
        Path dirPath = Paths.get(downloadDir);
        
        try {
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
        } catch (IOException e) {
            addLog("创建文件夹失败: " + sourcePath + " - " + e.getMessage());
        }
    }
}
