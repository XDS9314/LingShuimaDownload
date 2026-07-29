//版权声明
//本软件由作者:灵枢 开发，仅供学习交流使用
//严禁任何形式的非法破解、反编译、逆向工程等行为
//使用者应当遵守相关法律法规，尊重知识产权
//作者:灵枢 保留所有权利

package com.lingshu.ima.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lingshu.ima.dto.helper.SearchKBInfo;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResponse {

    private int code;
    private String msg;

    @JsonProperty("infos")
    private List<SearchKBInfo> knowledgeBaseList;

    @JsonProperty("is_end")
    private boolean hasMore;

    @JsonProperty("next_cursor")
    private String nextCursor;

    private String queryId;
    private String requestId;

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }

    public List<SearchKBInfo> getKnowledgeBaseList() { return knowledgeBaseList; }
    public void setKnowledgeBaseList(List<SearchKBInfo> knowledgeBaseList) { this.knowledgeBaseList = knowledgeBaseList; }

    public boolean isHasMore() { return hasMore; }
    public void setHasMore(boolean hasMore) { this.hasMore = hasMore; }

    public String getNextCursor() { return nextCursor; }
    public void setNextCursor(String nextCursor) { this.nextCursor = nextCursor; }

    public String getQueryId() { return queryId; }
    public void setQueryId(String queryId) { this.queryId = queryId; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public int getTotalCount() {
        return knowledgeBaseList != null ? knowledgeBaseList.size() : 0;
    }

    public boolean isSuccess() {
        return code == 0;
    }
}
