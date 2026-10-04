package com.hadluo.entity.vo;

import java.io.Serializable;

/**
 * 题目类型查询范围：来自 timuxinxi 表，按教师或专业过滤。
 */
public class TopicTypeScope implements Serializable {

    private static final long serialVersionUID = 1L;

    private String jiaoshigonghao;
    private String zhuanye;

    public String getJiaoshigonghao() {
        return jiaoshigonghao;
    }

    public void setJiaoshigonghao(String jiaoshigonghao) {
        this.jiaoshigonghao = jiaoshigonghao;
    }

    public String getZhuanye() {
        return zhuanye;
    }

    public void setZhuanye(String zhuanye) {
        this.zhuanye = zhuanye;
    }
}
