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
    OPERATOR_AUTHENTICATION("10100301","import","authentication","运营商安全验证。点击安全验证对话框的“确定”按钮"),
    OPERATOR_SUCCESS("10100401","importSuccess","success","点击“导入成功”按钮"),

    OPERATOR_LOGIN("10900101","login","login","运营商登录"),
    OPERATOR_CRAWLER_START("10900201","crawler","start","爬虫启动"),
    OPERATOR_CRAWLER_SMS_VERIFY("10900202","crawler","sms_verify","触发短信验证"),
    OPERATOR_DATA_CLEAN("10900301","data","clean","数据清洗"),
    OPERATOR_DATA_STORAGE("10900302","data","storage","数据入库"),
    OPERATOR_CALLBACK_NOTIFY("10900401","callback","notify","回调通知"),


    /**
     * 电商
     */
    ECOMMERCE_CHOOSE_TAOBAO("30100101","loginBefore","start","选择淘宝。点击“淘宝”选项"),
    ECOMMERCE_CHOOSE_ZHIFUBAO("30100102","loginBefore","start","选择支付宝。点击“支付宝”选项"),
    ECOMMERCE_LOOK_AUTHORIZATION_SDK("30100103","loginBefore","start"," SDK点击查看个人信息查询授权书页面"),
    ECOMMERCE_CANCEL_AUTHORIZATION("30100104","loginBefore","cancel_authorization","退出个人信息查询授权书页面"),
    ECOMMERCE_LIST_CANCEL("30100105","loginBefore","exit_authorization_page","退出当前页面。点击左上角“<\"按钮"),
    ECOMMERCE_THIRDPARTY_LOGIN_CANCEL("30100201","loginBefore","exit_authorization_page","退出当前第三方登录页面。点击左上角“<\"按钮"),
    ECOMMERCE_AUTHORIZATION_QRCODE("30100301","loginBefore","get_qrcode","点击重新获得二维码"),
    ECOMMERCE_AUTHORIZATION_("30100302","loginBefore","","点击手机淘宝一键认证"),
    ECOMMERCE_LOOK_AUTHORIZATION_H5("30100303","loginBefore","look_authorization","H5点击查看个人信息查询授权书页面"),
    ECOMMERCE_EXIT_AUTHORIZATION("30100304","loginBefore","exit_authorization","退出个人信息查询授权书页面。点击左上角“<\"按钮"),
    ECOMMERCE_AUTHORIZATION_FAQ("30100305","loginBefore","faq","查看FAQ"),
    ECOMMERCE_AUTHORIZATION_FEEDBACK("30100306","loginBefore","feedback","意见反馈"),
    ECOMMERCE_AUTHENTICATION("30100401","import","authentication","支付宝安全验证,点击安全验证对话框的“确定”按钮"),
    ECOMMERCE_BREAK("30100402","import","break","中断电商导入并退出当前页。退出对话框中，点击“确定退出”按钮"),
    ECOMMERCE_SUCCESS("30100501","importSuccess","success","点击“导入成功”按钮"),
    ECOMMERCE_EXIT("30100502","importSuccess","exit","退出当前页面。点击左上角“<\"按钮"),

    ECOMMERCE_LOGIN("30900102","login","login","电商登录"),
    ECOMMERCE_CRAWLER_START("30900201","crawler","start","爬虫启动"),
    ECOMMERCE_CRAWLER_SMS_VERIFY("30900202","crawler","sms_verify","触发短信验证"),
    ECOMMERCE_DATA_CLEAN("30900301","data","clean","数据清洗"),
    ECOMMERCE_DATA_STORAGE("30900302","data","storage","数据入库"),
    ECOMMERCE_CALLBACK_NOTIFY("30900401","callback","notify","回调通知");







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
