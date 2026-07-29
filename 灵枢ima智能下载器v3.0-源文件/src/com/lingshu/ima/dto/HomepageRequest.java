//版权声明
//本软件由作者:灵枢 开发，仅供学习交流使用
//严禁任何形式的非法破解、反编译、逆向工程等行为
//使用者应当遵守相关法律法规，尊重知识产权
//作者:灵枢 保留所有权利

package com.lingshu.ima.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HomepageRequest {

    @JsonProperty("hot_offset")
    private int offset;
    @JsonProperty("hot_limit")
    private int limit;

    @JsonProperty("recommend_limit")
    private int recommendLimit = 10;

    @JsonProperty("new_limit")
    private int newLimit = 10;

    public HomepageRequest() {}

    public HomepageRequest(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public int getOffset() { return offset; }
    public void setOffset(int offset) { this.offset = offset; }

    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }

    public int getRecommendLimit() { return recommendLimit; }
    public void setRecommendLimit(int recommendLimit) { this.recommendLimit = recommendLimit; }

    public int getNewLimit() { return newLimit; }
    public void setNewLimit(int newLimit) { this.newLimit = newLimit; }
}
