//版权声明
//本软件由作者:灵枢 开发，仅供学习交流使用
//严禁任何形式的非法破解、反编译、逆向工程等行为
//使用者应当遵守相关法律法规，尊重知识产权
//作者:灵枢 保留所有权利

package com.lingshu.ima.dto;

public class SearchRequest {

    private String keyword;
    private int offset;
    private int limit;
    private String filter;

    public SearchRequest() {}

    public SearchRequest(String query, int offset, int limit) {
        this.keyword = query;
        this.offset = offset;
        this.limit = limit;
    }

    public String getKeyword() { return keyword; }
    public void setKeyword(String query) { this.keyword = query; }

    public int getOffset() { return offset; }
    public void setOffset(int offset) { this.offset = offset; }

    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }

    public String getFilter() { return filter; }
    public void setFilter(String filter) { this.filter = filter; }
}
