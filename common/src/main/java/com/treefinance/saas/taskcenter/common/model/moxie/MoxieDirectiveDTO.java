package com.treefinance.saas.taskcenter.common.model.moxie;


import com.treefinance.saas.taskcenter.common.model.dto.DirectiveDTO;

/**
 * Created by haojiahong on 2017/9/14.
 */
public class MoxieDirectiveDTO extends DirectiveDTO {

    private static final long serialVersionUID = 6201076878996673264L;

    private String moxieTaskId;

    public String getMoxieTaskId() {
        return moxieTaskId;
    }

    public void setMoxieTaskId(String moxieTaskId) {
        this.moxieTaskId = moxieTaskId;
    }
}
