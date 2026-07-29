//版权声明
//本软件由作者:灵枢 开发，仅供学习交流使用
//严禁任何形式的非法破解、反编译、逆向工程等行为
//使用者应当遵守相关法律法规，尊重知识产权
//作者:灵枢 保留所有权利

package com.lingshu.ima.dto.helper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchKBInfo {

    @JsonProperty("knowledge_base_id")
    private String knowledgeBaseId;

    @JsonProperty("name")
    private String title;

    private String description;

    @JsonProperty("cover")
    private String coverUrl;

    @JsonProperty("publisher")
    private String authorName;

    private String authorAvatarUrl;

    @JsonProperty("media_count")
    private int fileCount;

    @JsonProperty("member_count")
    private int readCount;

    private int shareCount;
    private int likeCount;
    private String updateTime;
    private String mediaTypeInfo;
    private String shareId;
    private int joinType;

    public String getKnowledgeBaseId() { return knowledgeBaseId; }
    public void setKnowledgeBaseId(String knowledgeBaseId) { this.knowledgeBaseId = knowledgeBaseId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getAuthorAvatarUrl() { return authorAvatarUrl; }
    public void setAuthorAvatarUrl(String authorAvatarUrl) { this.authorAvatarUrl = authorAvatarUrl; }

    public int getFileCount() { return fileCount; }
    public void setFileCount(int fileCount) { this.fileCount = fileCount; }

    public int getReadCount() { return readCount; }
    public void setReadCount(int readCount) { this.readCount = readCount; }

    public int getShareCount() { return shareCount; }
    public void setShareCount(int shareCount) { this.shareCount = shareCount; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public String getUpdateTime() { return updateTime; }
    public void setUpdateTime(String updateTime) { this.updateTime = updateTime; }

    public String getMediaTypeInfo() { return mediaTypeInfo; }
    public void setMediaTypeInfo(String mediaTypeInfo) { this.mediaTypeInfo = mediaTypeInfo; }

    public String getShareId() { return shareId; }
    public void setShareId(String shareId) { this.shareId = shareId; }

    public int getJoinType() { return joinType; }
    public void setJoinType(int joinType) { this.joinType = joinType; }

    @Override
    public String toString() {
        return title + " (" + fileCount + " files, " + readCount + " members)";
    }
}
