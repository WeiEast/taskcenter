package com.treefinance.saas.taskcenter.common.enums;

import java.util.Objects;

/**
 * @author 张琰佳
 * @since 7:37 PM 2019/1/23
 */
public enum CodeStepEnum {
    /**
     * 运营商
     */
    OPERATOR_START("10100101","loginBefore","start","运营商流程启动，加载首页"),
    OPERATOR_CHOOSE_LIST("10100102","loginBefore","choose_list","点击运营商，打开运营商选择列表"),
    OPERATOR_LIST_SURE("10100103","loginBefore","list_sure","运营商选择列表页面，点击确定"),
    OPERATOR_LIST_CANCEL("10100104","loginBefore","list_cancel","运营商选择列表页面，点击取消"),
    OPERATOR_MOBILE_SURE("10100105","loginBefore","mobile_sure","确认并提交运营商信息。点击“确认手机号及运营商无误”按钮"),
    OPERATOR_SELECT_AUTHORIZATION("10100106","loginBefore","select_authorization","勾选个人信息查询授权书"),
    OPERATOR_CANCEL_AUTHORIZATION("10100107","loginBefore","cancel_authorization","取消勾选个人信息查询授权书"),
    OPERATOR_LOOK_AUTHORIZATION("10100108","loginBefore","look_authorization","点击查看个人信息查询授权书页面"),
    OPERATOR_EXIT_AUTHORIZATION("10100109","loginBefore","exit_authorization","退出个人信息查询授权书页面"),
    OPERATOR_EXIT_AUTHORIZATION_PAGE("10100110","loginBefore","exit_authorization_page","退出当前页面。点击左上角“<\"按钮"),
    OPERATOR_MAINTAIN("10100111","loginBefore","maintain","后台反馈运营商正在维护，用户点击确认"),
    OPERATOR_FIND_PWD("10100201","loginBefore","find_pwd","找回服务密码。打卡的找回服务密码对话框中，点击“确定”按钮"),
    OPERATOR_NEXT("10100202","loginBefore","next","开始导入运营商。点击“下一步”按钮"),
    OPERATOR_LAST("10100203","loginBefore","last","退出当前页面。点击下方返回上一步按钮"),
    OPERATOR_FAQ("10100204","loginBefore","faq","查看FAQ"),
    OPERATOR_FEEDBACK("10100205","loginBefore","feedback","意见反馈"),
    operator_Authentication("10100301","import","authentication","运营商安全验证。点击安全验证对话框的“确定”按钮"),
    operator_success("10100401","importSuccess","success","点击“导入成功”按钮"),

    operator_login("10900101","login","login","运营商登录"),
    operator_crawler_start("10900201","crawler","stsrt","爬虫启动"),
    operator_crawler_sms_verify("10900202","crawler","sms_verify","触发短信验证"),
    operator_data_clean("10900301","data","clean","数据清洗"),
    operator_data_storage("10900302","data","storage","数据入库"),
    operator_callback_notify("10900401","callback","notify","回调通知");
    private String code;

    private String step;
    private String subStep;

    private String msg;

    CodeStepEnum(String code, String step,String subStep,String msg) {
        this.code = code;
        this.step = step;
        this.subStep=subStep;
        this.msg=msg;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getSubStep() {
        return subStep;
    }

    public void setSubStep(String subStep) {
        this.subStep = subStep;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static String getStep(String key) {
        if (Objects.nonNull(key)) {
            for (CodeStepEnum item : CodeStepEnum.values()) {
                if (key.equals(item.getCode())) {
                    return item.getStep();
                }
            }
        }
        return null;
    }

    public static String getSubStep(String key) {
        if (Objects.nonNull(key)) {
            for (CodeStepEnum item : CodeStepEnum.values()) {
                if (key.equals(item.getCode())) {
                    return item.getSubStep();
                }
            }
        }
        return null;
    }

    public static String getMsg(String key) {
        if (Objects.nonNull(key)) {
            for (CodeStepEnum item : CodeStepEnum.values()) {
                if (key.equals(item.getCode())) {
                    return item.getMsg();
                }
            }
        }
        return null;
    }
}
