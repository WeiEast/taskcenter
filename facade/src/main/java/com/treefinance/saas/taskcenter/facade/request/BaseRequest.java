package com.treefinance.saas.taskcenter.facade.request;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;


/**
 * @author:guoguoyun
 * @date:Created in 2018/9/18上午11:10
 */
public class BaseRequest implements Serializable{
    public BaseRequest() {
    }
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
