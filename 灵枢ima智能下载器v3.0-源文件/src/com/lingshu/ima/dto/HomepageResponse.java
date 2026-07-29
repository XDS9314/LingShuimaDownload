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
public class HomepageResponse {

    private int code;
    private String msg;

    @JsonProperty("recommend_list")
    private KBSection recommendSection;

    @JsonProperty("hot_list")
    private KBSection hotSection;

    private boolean needShowGuide;

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }

    public KBSection getRecommendSection() { return recommendSection; }
    public void setRecommendSection(KBSection recommendSection) { this.recommendSection = recommendSection; }

    public KBSection getHotSection() { return hotSection; }
    public void setHotSection(KBSection hotSection) { this.hotSection = hotSection; }

    public boolean isNeedShowGuide() { return needShowGuide; }
    public void setNeedShowGuide(boolean needShowGuide) { this.needShowGuide = needShowGuide; }

    public List<SearchKBInfo> getRecommendList() {
        return recommendSection != null ? recommendSection.getInfos() : null;
    }

    public List<SearchKBInfo> getHotList() {
        return hotSection != null ? hotSection.getInfos() : null;
    }

    public boolean isSuccess() {
        return code == 0;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KBSection {
        @JsonProperty("infos")
        private List<SearchKBInfo> infos;

        @JsonProperty("is_end")
        private boolean isEnd;

        @JsonProperty("next_cursor")
        private String nextCursor;

        public List<SearchKBInfo> getInfos() { return infos; }
        public void setInfos(List<SearchKBInfo> infos) { this.infos = infos; }

        public boolean isEnd() { return isEnd; }
        public void setEnd(boolean isEnd) { this.isEnd = isEnd; }

        public String getNextCursor() { return nextCursor; }
        public void setNextCursor(String nextCursor) { this.nextCursor = nextCursor; }
    }
}
