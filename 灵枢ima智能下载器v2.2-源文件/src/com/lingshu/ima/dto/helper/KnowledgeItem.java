//版权声明
//本软件由作者:灵枢 开发，仅供学习交流使用
//严禁任何形式的非法破解、反编译、逆向工程等行为
//使用者应当遵守相关法律法规，尊重知识产权
//作者:灵枢 保留所有权利

package com.lingshu.ima.dto.helper;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

//版权声明
//本软件由作者:灵枢 开发，仅供学习交流使用
//严禁任何形式的非法破解、反编译、逆向工程等行为
//使用者应当遵守相关法律法规，尊重知识产权
//作者:灵枢 保留所有权利

public class KnowledgeItem {
    @JsonProperty("abstract")
    private String itemAbstract;
    
    @JsonProperty("access_status")
    private int accessStatus;
    
    @JsonProperty("access_status_update_ts")
    private String accessStatusUpdateTs;
    
    @JsonProperty("cover_urls")
    private List<String> coverUrls;
    
    @JsonProperty("create_time")
    private String createTime;
    
    @JsonProperty("file_size")
    private String fileSize;
    
    @JsonProperty("folder_info")
    private FolderInfo folderInfo;
    
    @JsonProperty("forbidden_info")
    private Object forbiddenInfo;
    
    @JsonProperty("highlight_tags")
    private List<Object> highlightTags;
    
    private String introduction;
    
    @JsonProperty("is_repeated")
    private boolean isRepeated;
    
    @JsonProperty("is_top")
    private Boolean isTop;
    
    @JsonProperty("ai_key_points_available")
    private Boolean aiKeyPointsAvailable;
    
    @JsonProperty("jump_url")
    private String jumpUrl;
    
    @JsonProperty("last_modify_time")
    private String lastModifyTime;
    
    @JsonProperty("last_open_time")
    private String lastOpenTime;
    
    private String logo;
    
    @JsonProperty("md5_sum")
    private String md5Sum;
    
    @JsonProperty("media_audit_status")
    private String mediaAuditStatus;
    
    @JsonProperty("media_id")
    private String mediaId;
    
    @JsonProperty("media_state")
    private String mediaState;
    
    @JsonProperty("media_type")
    private String mediaType;
    
    @JsonProperty("media_type_info")
    private MediaTypeInfo mediaTypeInfo;
    
    @JsonProperty("parent_folder_id")
    private String parentFolderId;
    
    @JsonProperty("parse_err_info")
    private String parseErrInfo;
    
    @JsonProperty("parse_progress")
    private String parseProgress;
    
    @JsonProperty("parsed_file_url")
    private String parsedFileUrl;
    
    private String password;
    
    @JsonProperty("raw_file_url")
    private String rawFileUrl;
    
    private String secondIndex;
    
    @JsonProperty("sound_recording_duration")
    private String soundRecordingDuration;
    
    @JsonProperty("source_path")
    private String sourcePath;
    
    @JsonProperty("sub_media_type")
    private int subMediaType;
    
    @JsonProperty("summary_state")
    private String summaryState;
    
    private List<String> tags;
    
    private String title;
    
    @JsonProperty("time_wording")
    private String timeWording;
    
    @JsonProperty("update_time")
    private String updateTime;
    
    //版权声明
    //本软件由作者:灵枢 开发，仅供学习交流使用
    //严禁任何形式的非法破解、反编译、逆向工程等行为
    //使用者应当遵守相关法律法规，尊重知识产权
    //作者:灵枢 保留所有权利
    
    public String getItemAbstract() {
        return itemAbstract;
    }
    
    public void setItemAbstract(String itemAbstract) {
        this.itemAbstract = itemAbstract;
    }
    
    public int getAccessStatus() {
        return accessStatus;
    }
    
    public void setAccessStatus(int accessStatus) {
        this.accessStatus = accessStatus;
    }
    
    public String getAccessStatusUpdateTs() {
        return accessStatusUpdateTs;
    }
    
    public void setAccessStatusUpdateTs(String accessStatusUpdateTs) {
        this.accessStatusUpdateTs = accessStatusUpdateTs;
    }
    
    public List<String> getCoverUrls() {
        return coverUrls;
    }
    
    public void setCoverUrls(List<String> coverUrls) {
        this.coverUrls = coverUrls;
    }
    
    public String getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
    
    public String getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }
    
    public FolderInfo getFolderInfo() {
        return folderInfo;
    }
    
    public void setFolderInfo(FolderInfo folderInfo) {
        this.folderInfo = folderInfo;
    }
    
    public Object getForbiddenInfo() {
        return forbiddenInfo;
    }
    
    public void setForbiddenInfo(Object forbiddenInfo) {
        this.forbiddenInfo = forbiddenInfo;
    }
    
    public List<Object> getHighlightTags() {
        return highlightTags;
    }
    
    public void setHighlightTags(List<Object> highlightTags) {
        this.highlightTags = highlightTags;
    }
    
    public String getIntroduction() {
        return introduction;
    }
    
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
    
    public boolean isRepeated() {
        return isRepeated;
    }
    
    public void setRepeated(boolean repeated) {
        isRepeated = repeated;
    }
    
    public Boolean getIsTop() {
        return isTop;
    }
    
    public void setIsTop(Boolean isTop) {
        this.isTop = isTop;
    }
    
    public Boolean getAiKeyPointsAvailable() {
        return aiKeyPointsAvailable;
    }
    
    public void setAiKeyPointsAvailable(Boolean aiKeyPointsAvailable) {
        this.aiKeyPointsAvailable = aiKeyPointsAvailable;
    }
    
    public String getJumpUrl() {
        return jumpUrl;
    }
    
    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }
    
    public String getLastModifyTime() {
        return lastModifyTime;
    }
    
    public void setLastModifyTime(String lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }
    
    public String getLastOpenTime() {
        return lastOpenTime;
    }
    
    public void setLastOpenTime(String lastOpenTime) {
        this.lastOpenTime = lastOpenTime;
    }
    
    public String getLogo() {
        return logo;
    }
    
    public void setLogo(String logo) {
        this.logo = logo;
    }
    
    public String getMd5Sum() {
        return md5Sum;
    }
    
    public void setMd5Sum(String md5Sum) {
        this.md5Sum = md5Sum;
    }
    
    public String getMediaAuditStatus() {
        return mediaAuditStatus;
    }
    
    public void setMediaAuditStatus(String mediaAuditStatus) {
        this.mediaAuditStatus = mediaAuditStatus;
    }
    
    public String getMediaId() {
        return mediaId;
    }
    
    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }
    
    public String getMediaState() {
        return mediaState;
    }
    
    public void setMediaState(String mediaState) {
        this.mediaState = mediaState;
    }
    
    public String getMediaType() {
        return mediaType;
    }
    
    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
    
    public MediaTypeInfo getMediaTypeInfo() {
        return mediaTypeInfo;
    }
    
    public void setMediaTypeInfo(MediaTypeInfo mediaTypeInfo) {
        this.mediaTypeInfo = mediaTypeInfo;
    }
    
    public String getParentFolderId() {
        return parentFolderId;
    }
    
    public void setParentFolderId(String parentFolderId) {
        this.parentFolderId = parentFolderId;
    }
    
    public String getParseErrInfo() {
        return parseErrInfo;
    }
    
    public void setParseErrInfo(String parseErrInfo) {
        this.parseErrInfo = parseErrInfo;
    }
    
    public String getParseProgress() {
        return parseProgress;
    }
    
    public void setParseProgress(String parseProgress) {
        this.parseProgress = parseProgress;
    }
    
    public String getParsedFileUrl() {
        return parsedFileUrl;
    }
    
    public void setParsedFileUrl(String parsedFileUrl) {
        this.parsedFileUrl = parsedFileUrl;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getRawFileUrl() {
        return rawFileUrl;
    }
    
    public void setRawFileUrl(String rawFileUrl) {
        this.rawFileUrl = rawFileUrl;
    }
    
    public String getSecondIndex() {
        return secondIndex;
    }
    
    public void setSecondIndex(String secondIndex) {
        this.secondIndex = secondIndex;
    }
    
    public String getSoundRecordingDuration() {
        return soundRecordingDuration;
    }
    
    public void setSoundRecordingDuration(String soundRecordingDuration) {
        this.soundRecordingDuration = soundRecordingDuration;
    }
    
    public String getSourcePath() {
        return sourcePath;
    }
    
    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }
    
    public int getSubMediaType() {
        return subMediaType;
    }
    
    public void setSubMediaType(int subMediaType) {
        this.subMediaType = subMediaType;
    }
    
    public String getSummaryState() {
        return summaryState;
    }
    
    public void setSummaryState(String summaryState) {
        this.summaryState = summaryState;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getTimeWording() {
        return timeWording;
    }
    
    public void setTimeWording(String timeWording) {
        this.timeWording = timeWording;
    }
    
    public String getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
    
    //版权声明
    //本软件由作者:灵枢 开发，仅供学习交流使用
    //严禁任何形式的非法破解、反编译、逆向工程等行为
    //使用者应当遵守相关法律法规，尊重知识产权
    //作者:灵枢 保留所有权利
}
