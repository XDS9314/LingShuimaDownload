//版权声明
//本软件由作者:灵枢 开发，仅供学习交流使用
//严禁任何形式的非法破解、反编译、逆向工程等行为
//使用者应当遵守相关法律法规，尊重知识产权
//作者:灵枢 保留所有权利

package com.lingshu.ima.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lingshu.ima.dto.helper.CurrentPath;
import com.lingshu.ima.dto.helper.KnowledgeBaseInfo;
import com.lingshu.ima.dto.helper.KnowledgeItem;
import com.lingshu.ima.dto.helper.VersionMessage;
import java.util.List;

//版权声明
//本软件由作者:灵枢 开发，仅供学习交流使用
//严禁任何形式的非法破解、反编译、逆向工程等行为
//使用者应当遵守相关法律法规，尊重知识产权
//作者:灵枢 保留所有权利

public class ShareInfoResponse {
    private int code;
    
    @JsonProperty("current_path")
    private List<CurrentPath> currentPath;
    
    private boolean initializing;
    
    @JsonProperty("is_end")
    private boolean isEnd;
    
    @JsonProperty("is_in_apply_list")
    private boolean isInApplyList;
    
    @JsonProperty("is_update")
    private boolean isUpdate;
    
    @JsonProperty("knowledge_base_info")
    private KnowledgeBaseInfo knowledgeBaseInfo;
    
    @JsonProperty("knowledge_list")
    private List<KnowledgeItem> knowledgeList;
    
    private String msg;
    
    @JsonProperty("next_cursor")
    private String nextCursor;
    
    @JsonProperty("total_size")
    private String totalSize;
    
    private String version;
    
    @JsonProperty("version_message")
    private VersionMessage versionMessage;
    
    //版权声明
    //本软件由作者:灵枢 开发，仅供学习交流使用
    //严禁任何形式的非法破解、反编译、逆向工程等行为
    //使用者应当遵守相关法律法规，尊重知识产权
    //作者:灵枢 保留所有权利
    
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    public List<CurrentPath> getCurrentPath() {
        return currentPath;
    }
    
    public void setCurrentPath(List<CurrentPath> currentPath) {
        this.currentPath = currentPath;
    }
    
    public boolean isInitializing() {
        return initializing;
    }
    
    public void setInitializing(boolean initializing) {
        this.initializing = initializing;
    }
    
    public boolean isEnd() {
        return isEnd;
    }
    
    public void setEnd(boolean end) {
        isEnd = end;
    }
    
    public void setInApplyList(boolean inApplyList) {
        isInApplyList = inApplyList;
    }
    
    public void setUpdate(boolean update) {
        isUpdate = update;
    }
    
    public KnowledgeBaseInfo getKnowledgeBaseInfo() {
        return knowledgeBaseInfo;
    }
    
    public void setKnowledgeBaseInfo(KnowledgeBaseInfo knowledgeBaseInfo) {
        this.knowledgeBaseInfo = knowledgeBaseInfo;
    }
    
    public List<KnowledgeItem> getKnowledgeList() {
        return knowledgeList;
    }
    
    public void setKnowledgeList(List<KnowledgeItem> knowledgeList) {
        this.knowledgeList = knowledgeList;
    }
    
    public String getMsg() {
        return msg;
    }
    
    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    public String getNextCursor() {
        return nextCursor;
    }
    
    public void setNextCursor(String nextCursor) {
        this.nextCursor = nextCursor;
    }
    
    public String getTotalSize() {
        return totalSize;
    }
    
    public void setTotalSize(String totalSize) {
        this.totalSize = totalSize;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public VersionMessage getVersionMessage() {
        return versionMessage;
    }
    
    public void setVersionMessage(VersionMessage versionMessage) {
        this.versionMessage = versionMessage;
    }
    
    //版权声明
    //本软件由作者:灵枢 开发，仅供学习交流使用
    //严禁任何形式的非法破解、反编译、逆向工程等行为
    //使用者应当遵守相关法律法规，尊重知识产权
    //作者:灵枢 保留所有权利
}
