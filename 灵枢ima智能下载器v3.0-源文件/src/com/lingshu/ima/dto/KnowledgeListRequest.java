//版权声明
//本软件由作者:灵枢 开发，仅供学习交流使用
//严禁任何形式的非法破解、反编译、逆向工程等行为
//使用者应当遵守相关法律法规，尊重知识产权
//作者:灵枢 保留所有权利

package com.lingshu.ima.dto;

public class KnowledgeListRequest {

    private String knowledge_base_id;
    private String parent_folder_id;
    private String cursor;
    private int limit;

    public KnowledgeListRequest() {}

    public KnowledgeListRequest(String knowledgeBaseId, String parentFolderId, String cursor, int limit) {
        this.knowledge_base_id = knowledgeBaseId;
        this.parent_folder_id = parentFolderId;
        this.cursor = cursor;
        this.limit = limit;
    }

    public String getKnowledge_base_id() { return knowledge_base_id; }
    public void setKnowledge_base_id(String knowledge_base_id) { this.knowledge_base_id = knowledge_base_id; }

    public String getParent_folder_id() { return parent_folder_id; }
    public void setParent_folder_id(String parent_folder_id) { this.parent_folder_id = parent_folder_id; }

    public String getCursor() { return cursor; }
    public void setCursor(String cursor) { this.cursor = cursor; }

    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }
}
