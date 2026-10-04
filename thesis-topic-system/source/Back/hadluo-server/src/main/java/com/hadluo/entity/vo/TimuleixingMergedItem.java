package com.hadluo.entity.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * 题目类型合并列表项：字典表 + 题目信息表中的类型。
 */
public class TimuleixingMergedItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /** timuleixing 表主键；仅来自题目信息时为空 */
    private Long id;

    private String timuleixing;

    /** 题目信息中使用该类型的题目数量 */
    private Integer topicCount;

    /**
     * 来源：manual=仅字典维护，topic=仅题目信息，both=两者均有
     */
    private String source;

    private Date addtime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTimuleixing() {
        return timuleixing;
    }

    public void setTimuleixing(String timuleixing) {
        this.timuleixing = timuleixing;
    }

    public Integer getTopicCount() {
        return topicCount;
    }

    public void setTopicCount(Integer topicCount) {
        this.topicCount = topicCount;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Date getAddtime() {
        return addtime;
    }

    public void setAddtime(Date addtime) {
        this.addtime = addtime;
    }
}
