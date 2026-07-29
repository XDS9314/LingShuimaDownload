//版权声明
//本软件由作者:灵枢 开发，仅供学习交流使用
//严禁任何形式的非法破解、反编译、逆向工程等行为
//使用者应当遵守相关法律法规，尊重知识产权
//作者:灵枢 保留所有权利

package com.lingshu.ima.service;

import com.lingshu.ima.config.AppConfig;
import com.lingshu.ima.dto.helper.KnowledgeItem;
import com.lingshu.ima.dto.helper.SearchKBInfo;
import com.lingshu.ima.dto.HomepageResponse;
import com.lingshu.ima.dto.SearchResponse;
import java.util.ArrayList;
import java.util.List;

public class KnowledgeBrowserService {

    private final ApiService apiService;
    private final AppConfig config;

    public interface BrowseCallback {
        void onSearchResult(List<SearchKBInfo> results, int totalCount);
        void onHomepageResult(HomepageResponse response);
        void onBrowseFolder(List<KnowledgeItem> items, String folderName);
        void onError(String message);
        void onLoading(boolean loading);
    }

    private static String safeMsg(Exception e) {
        String msg = e.getMessage();
        if (msg == null || msg.isEmpty()) {
            return e.getClass().getSimpleName();
        }
        return msg;
    }

    public KnowledgeBrowserService() {
        this.apiService = new ApiService();
        this.config = AppConfig.getInstance();
    }

    public ApiService getApiService() {
        return apiService;
    }

    public void searchAsync(final String query, final BrowseCallback callback) {
        new Thread(() -> {
            try {
                callback.onLoading(true);
                SearchResponse response = apiService.searchKnowledgeBase(query, 0, 20);
                if (response.isSuccess() && response.getKnowledgeBaseList() != null) {
                    callback.onSearchResult(response.getKnowledgeBaseList(), response.getTotalCount());
                } else {
                    callback.onError("搜索无结果");
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onError("搜索失败: " + safeMsg(e));
            } finally {
                callback.onLoading(false);
            }
        }).start();
    }

    public void loadHomepageAsync(final BrowseCallback callback) {
        new Thread(() -> {
            try {
                callback.onLoading(true);
                HomepageResponse response = apiService.getHomepage(0, 20);
                if (response.isSuccess()) {
                    callback.onHomepageResult(response);
                } else {
                    callback.onError("加载首页失败: " + response.getMsg());
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onError("加载首页失败: " + safeMsg(e));
            } finally {
                callback.onLoading(false);
            }
        }).start();
    }

    public void browseKnowledgeBaseAsync(final String knowledgeBaseId, final String folderName, final BrowseCallback callback) {
        new Thread(() -> {
            try {
                callback.onLoading(true);
                List<KnowledgeItem> items = apiService.fetchKnowledgeBaseAll(knowledgeBaseId);
                callback.onBrowseFolder(items, folderName);
            } catch (Exception e) {
                e.printStackTrace();
                callback.onError("浏览知识库失败: " + safeMsg(e));
            } finally {
                callback.onLoading(false);
            }
        }).start();
    }

    public void browseFolderAsync(final String knowledgeBaseId, final String parentFolderId, final String folderName, final BrowseCallback callback) {
        new Thread(() -> {
            try {
                callback.onLoading(true);
                com.lingshu.ima.dto.KnowledgeListResponse response = apiService.getKnowledgeList(knowledgeBaseId, parentFolderId, "", config.getDownloadBatchFetchLimit());
                if (response.isSuccess() && response.getKnowledge_list() != null) {
                    List<KnowledgeItem> items = new ArrayList<>(response.getKnowledge_list());
                    // 深度递归获取所有子文件夹内容 (最多10层, 用独立列表避免ConcurrentModificationException)
                    List<KnowledgeItem> subItems = new ArrayList<>();
                    recursiveFetchSubFolders(knowledgeBaseId, items, subItems, 0, 10);
                    items.addAll(subItems);
                    callback.onBrowseFolder(items, folderName);
                } else {
                    callback.onError("文件夹为空或加载失败");
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onError("浏览文件夹失败: " + safeMsg(e));
            } finally {
                callback.onLoading(false);
            }
        }).start();
    }

    private void recursiveFetchSubFolders(String kbId, List<KnowledgeItem> parentItems, List<KnowledgeItem> accumulator, int depth, int maxDepth) {
        if (depth >= maxDepth) return;
        List<KnowledgeItem> nextLevelFolders = new ArrayList<>();
        for (KnowledgeItem item : parentItems) {
            if (isFolder(item) && item.getMediaId() != null) {
                try {
                    com.lingshu.ima.dto.KnowledgeListResponse subResponse = apiService.getKnowledgeList(kbId, item.getMediaId(), "", config.getDownloadBatchFetchLimit());
                    if (subResponse.isSuccess() && subResponse.getKnowledge_list() != null) {
                        for (KnowledgeItem subItem : subResponse.getKnowledge_list()) {
                            String basePath = item.getTitle() != null ? item.getTitle() : "";
                            String subPath = subItem.getSourcePath() != null ? subItem.getSourcePath() : "";
                            subItem.setSourcePath(basePath + "/" + subPath);
                        }
                        accumulator.addAll(subResponse.getKnowledge_list());
                        // Collect sub-folders for next level
                        for (KnowledgeItem subItem : subResponse.getKnowledge_list()) {
                            if (isFolder(subItem) && subItem.getMediaId() != null) {
                                nextLevelFolders.add(subItem);
                            }
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("子文件夹加载跳过(L" + depth + "): " + item.getMediaId() + " - " + safeMsg(ex));
                }
            }
        }
        // Recurse for next level folders
        if (!nextLevelFolders.isEmpty()) {
            recursiveFetchSubFolders(kbId, nextLevelFolders, accumulator, depth + 1, maxDepth);
        }
    }

    private boolean isFolder(KnowledgeItem item) {
        if (item.getMediaId() != null && item.getMediaId().startsWith("folder_")) {
            return true;
        }
        if (item.getMediaTypeInfo() != null && "文件夹".equals(item.getMediaTypeInfo().getName())) {
            return true;
        }
        return false;
    }

}
