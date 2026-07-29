//版权声明
//本软件由作者:灵枢 开发，仅供学习交流使用
//严禁任何形式的非法破解、反编译、逆向工程等行为
//使用者应当遵守相关法律法规，尊重知识产权
//作者:灵枢 保留所有权利

package com.lingshu.ima.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lingshu.ima.dto.helper.KnowledgeItem;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KnowledgeListResponse {

    private int code;
    private String msg;
    @JsonProperty("knowledge_list")
    private List<KnowledgeItem> knowledge_list;
    @JsonProperty("is_end")
    private boolean is_end;
    @JsonProperty("next_cursor")
    private String nextCursor;
    @JsonProperty("total_size")
    private int totalCount;

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }

    public List<KnowledgeItem> getKnowledge_list() { return knowledge_list; }
    public void setKnowledge_list(List<KnowledgeItem> knowledge_list) { this.knowledge_list = knowledge_list; }

    /**
     * API返回is_end=true表示没有更多数据，对应has_more=false
     */
    public boolean isHas_more() { return !is_end; }

    @JsonProperty("is_end")
    public boolean isIs_end() { return is_end; }
    public void setIs_end(boolean is_end) { this.is_end = is_end; }

    @JsonProperty("next_cursor")
    public String getNextCursor() { return nextCursor; }
    @JsonProperty("next_cursor")
    public void setNextCursor(String nextCursor) { this.nextCursor = nextCursor; }

    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }

    public boolean isSuccess() {
        return code == 0;
    }
}
