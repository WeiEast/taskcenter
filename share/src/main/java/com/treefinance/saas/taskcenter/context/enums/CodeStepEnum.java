package com.treefinance.saas.taskcenter.context.enums;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author 张琰佳
 * @since 8:43 PM 2019/1/24
 */
public enum CodeStepEnum {

    /**
     * 运营商（手机运营商选择确认页）
     */
    OPERATOR_PRE_START("10100101", Step.PRE_LOGIN, "start", "运营商流程启动，加载首页"),
    OPERATOR_SELECT_OPEN("10100102", Step.PRE_LOGIN, "select_open", "点击运营商，打开运营商选择列表"),
    OPERATOR_SELECT_CONFIRM("10100103", Step.PRE_LOGIN, "select_confirm", "运营商选择列表页面，点击确定"),
    OPERATOR_SELECT_CANCEL("10100104", Step.PRE_LOGIN, "select_cancel", "运营商选择列表页面，点击取消"),
    OPERATOR_MOBILE_CONFIRM("10100105", Step.PRE_LOGIN, "mobile_confirm", "确认并提交运营商信息。点击“确认手机号及运营商无误”按钮"),
    OPERATOR_AUTHORIZATION_CHECK("10100106", Step.PRE_LOGIN, "authorization_check", "勾选个人信息查询授权书"),
    OPERATOR_AUTHORIZATION_UNCHECK("10100107", Step.PRE_LOGIN, "authorization_uncheck", "取消勾选个人信息查询授权书"),
    OPERATOR_AUTHORIZATION_OPEN("10100108", Step.PRE_LOGIN, "authorization_open", "点击查看个人信息查询授权书页面"),
    OPERATOR_AUTHORIZATION_CLOSE("10100109", Step.PRE_LOGIN, "authorization_close", "退出个人信息查询授权书页面"),
    OPERATOR_PRE_PAGE_EXIT("10100110", Step.PRE_LOGIN, "page_exit", "退出当前页面。点击左上角“<\"按钮"),
    OPERATOR_MAINTAIN("10100111", Step.PRE_LOGIN, "maintain", "后台反馈运营商正在维护，用户点击确认"),
    /**
     * 运营商（登录页）
     */
    OPERATOR_FORGOT_PWD("10100201", Step.LOGIN, "password_forgot", "找回服务密码。打卡的找回服务密码对话框中，点击“确定”按钮"),
    OPERATOR_SUBMIT("10100202", Step.LOGIN, "submit", "开始导入运营商。点击“下一步”按钮"),
    OPERATOR_STEP_BACK("10100203", Step.LOGIN, "step_back", "退出当前页面。点击下方返回上一步按钮"),
    /**
     * 意见反馈
     */
    OPERATOR_FAQ("10100204", Step.OTHER, "faq_open", "查看FAQ"),
    OPERATOR_FEEDBACK("10100205", Step.OTHER, "feedback", "意见反馈"),
    /**
     * 导入进度条页
     */
    OPERATOR_CERT_CHECK_CONFIRM("10100301", Step.CRAWL_DATA, "cert_check_confirm", "运营商安全验证。点击安全验证对话框的“确定”按钮"),
    /**
     * 导入成功页
     */
    OPERATOR_SUCCESS_CONFIRM("10100401", Step.COMPLETE, "success_confirm", "点击“导入成功”按钮"),
    /**
     * 导入失败页
     */
    OPERATOR_FAILURE_RETRY("10100501", Step.FAILURE, "failure_retry", "点击“重新导入”"),
    /**
     * 系统操作
     */
    OPERATOR_LOGIN_START("10900101", Step.LOGIN, "start", "运营商开始登录"),
    OPERATOR_LOGIN_SUCCESS("10900190", Step.LOGIN, "success", "运营商登录成功"),
    OPERATOR_CRAWL_PRE("10900200", Step.CRAWL_DATA, "prepare", "收到消息，准备爬虫"),
    OPERATOR_CRAWL_START("10900201", Step.CRAWL_DATA, "start", "启动爬虫，开始爬取"),
    OPERATOR_CRAWL_SMS_START("10900202", Step.CRAWL_DATA, "sms_start", "触发短信验证"),
    OPERATOR_CRAWL_SMS_RECEIVED("10900203", Step.CRAWL_DATA, "sms_received", "收到短信验证码"),
    OPERATOR_CRAWL_SMS_SUCCESS("10900204", Step.CRAWL_DATA, "sms_success", "短信验证成功"),
    OPERATOR_CRAWL_SUCCESS("10900290", Step.CRAWL_DATA, "success", "爬取成功"),
    OPERATOR_DATA_START("10900301", Step.PROCESS_DATA, "start", "数据开始清洗"),
    OPERATOR_DATA_SUCCESS("10900302", Step.PROCESS_DATA, "success", "数据入库"),
    OPERATOR_DATA_SNAPSHOT("10900303", Step.PROCESS_DATA, "snapshot", "生成回调数据快照"),
    OPERATOR_CALLBACK_START("10900401", Step.CALLBACK, "start", "回调通知开始"),
    OPERATOR_CALLBACK_SUCCESS("10900402", Step.CALLBACK, "success", "回调通知成功"),

    /**
     * 邮箱账单(邮箱列表选择页)
     */
    EMAIL_START("20100101", Step.PRE_LOGIN, "start", "邮箱账单流程启动，加载首页"),
    EMAIL_SELECT_QQ("20100102", Step.PRE_LOGIN, "qq_selected", "选择QQ邮箱。点击“QQ邮箱”"),
    EMAIL_SELECT_163("20100103", Step.PRE_LOGIN, "163_selected", "选择163邮箱。点击“163邮箱”"),
    EMAIL_SELECT_126("20100104", Step.PRE_LOGIN, "126_selected", "选择126邮箱。点击“126邮箱”"),
    EMAIL_SELECT_SINA("20100105", Step.PRE_LOGIN, "sina_selected", "选择新浪邮箱。点击“新浪邮箱”"),
    EMAIL_SELECT_139("20100106", Step.PRE_LOGIN, "139_selected", "选择139邮箱。点击“139邮箱”"),
    EMAIL_SELECT_QQ_EXMAIL("20100107", Step.PRE_LOGIN, "qq_exmail_selected", "选择企业邮箱。点击“企业邮箱”"),
    EMAIL_AUTHORIZATION_CHECK("20100108", Step.PRE_LOGIN, "authorization_check", "勾选个人信息查询授权书"),
    EMAIL_AUTHORIZATION_UNCHECK("20100109", Step.PRE_LOGIN, "authorization_uncheck", "取消勾选个人信息查询授权书"),
    EMAIL_AUTHORIZATION_OPEN("20100110", Step.PRE_LOGIN, "authorization_open", "点击查看个人信息查询授权书页面"),
    EMAIL_AUTHORIZATION_CLOSE("20100111", Step.PRE_LOGIN, "authorization_close", "退出个人信息查询授权书页面"),
    EMAIL_PRE_PAGE_EXIT("20100112", Step.PRE_LOGIN, "page_exit", "退出当前邮箱列表页面，点击左上角“<\"按钮"),
    /**
     * 第三方登录页
     */
    EMAIL_THIRD_LOGIN_EXIT("20100201", Step.LOGIN, "third_login_exit", "第三方登录页面，点击左上角“<\"按钮"),
    /**
     * 导入进度条页
     */
    EMAIL_CRAWL_EXIT_CONFIRM("20100301", Step.CRAWL_DATA, "exit_confirm", "中断邮箱导入并退出当前页。退出对话框中，点击“确定退出”按钮"),
    /**
     * 导入成功页
     */
    EMAIL_COMPLETE_PAGE_EXIT("20100401", Step.COMPLETE, "page_exit", "退出邮箱导入成功页面。点击左上角“<\"按钮"),
    EMAIL_SUCCESS_CONFIRM("20100402", Step.COMPLETE, "success_confirm", "点击“导入成功”按钮"),
    /**
     * 导入失败页
     */
    EMAIL_FAILURE_RETRY("20100501", Step.FAILURE, "failure_retry", "点击“重新导入”"),
    /**
     * 系统操作
     */
    EMAIL_LOGIN_START("20900101", Step.LOGIN, "start", "邮箱开始登录"),
    EMAIL_LOGIN_SUCCESS("20900190", Step.LOGIN, "success", "邮箱登录完成"),
    EMAIL_CRAWL_PRE("20900200", Step.CRAWL_DATA, "prepare", "收到消息，准备爬虫"),
    EMAIL_CRAWL_START("20900201", Step.CRAWL_DATA, "start", "启动爬虫，开始爬取"),
    EMAIL_CRAWL_SUCCESS("20900290", Step.CRAWL_DATA, "success", "爬取成功"),
    EMAIL_DATA_START("20900301", Step.PROCESS_DATA, "start", "数据开始清洗"),
    EMAIL_DATA_SUCCESS("20900302", Step.PROCESS_DATA, "success", "数据入库"),
    EMAIL_DATA_SNAPSHOT("20900303", Step.PROCESS_DATA, "snapshot", "生成回调数据快照"),
    EMAIL_CALLBACK_START("20900401", Step.CALLBACK, "start", "回调通知开始"),
    EMAIL_CALLBACK_SUCCESS("20900402", Step.CALLBACK, "success", "回调通知成功"),

    /**
     * 电商
     */
    ECOMMERCE_SELECT_TAOBAO("30100101", Step.PRE_LOGIN, "taobao_select", "选择淘宝。点击“淘宝”选项"),
    ECOMMERCE_SELECT_ALIPAY("30100102", Step.PRE_LOGIN, "alipay_select", "选择支付宝。点击“支付宝”选项"),
    ECOMMERCE_SDK_AUTHORIZATION_OPEN("30100103", Step.PRE_LOGIN, "sdk_authorization_open", " SDK点击查看个人信息查询授权书页面"),
    ECOMMERCE_SDK_AUTHORIZATION_CLOSE("30100104", Step.PRE_LOGIN, "sdk_authorization_close", "退出个人信息查询授权书页面"),
    ECOMMERCE_PER_PAGE_EXIT("30100105", Step.PRE_LOGIN, "page_exit", "退出当前页面。点击左上角“<\"按钮"),

    ECOMMERCE_THIRD_LOGIN_EXIT("30100201", Step.LOGIN, "third_login_exit", "退出当前第三方登录页面。点击左上角“<\"按钮"),
    /**
     * 二维码登录页
     */
    ECOMMERCE_TAOBAO_QRCODE_REFRESH("30100301", Step.LOGIN, "qrcode_refresh", "点击重新获得二维码"),
    ECOMMERCE_ONE_KEY("30100302", Step.LOGIN, "onekey_click", "点击手机淘宝一键认证"),
    ECOMMERCE_H5_AUTHORIZATION_OPEN("30100303", Step.LOGIN, "h5_authorization_open", "H5点击查看个人信息查询授权书页面"),
    ECOMMERCE_H5_AUTHORIZATION_CLOSE("30100304", Step.LOGIN, "h5_authorization_close", "退出个人信息查询授权书页面。点击左上角“<\"按钮"),
    ECOMMERCE_FAQ("30100305", Step.OTHER, "faq_open", "查看FAQ"),
    ECOMMERCE_FEEDBACK("30100306", Step.OTHER, "feedback", "意见反馈"),
    ECOMMERCE_LOGIN_CERT_CHECK_CONFIRM("30100307", Step.LOGIN, "cert_check_confirm", "支付宝安全验证,点击安全验证对话框的“确定”按钮"),
    /**
     * 账号密码登录页
     */
    ECOMMERCE_TAOBAO_PAGE_LOADED("30100601", Step.LOGIN, "page_loaded", "淘宝授权登录页加载完成"),
    ECOMMERCE_TAOBAO_USERNAME_INPUT("30100602", Step.LOGIN, "username_input", "点击账号输入框"),
    ECOMMERCE_TAOBAO_PWD_INPUT("30100603", Step.LOGIN, "password_input", "点击密码输入框"),
    ECOMMERCE_TAOBAO_SUBMIT("30100604", Step.LOGIN, "submit", "淘宝账号密码页点击登录按钮"),
    /**
     * 导入进度页
     */
    ECOMMERCE_CRAWL_CERT_CHECK_CONFIRM("30100401", Step.CRAWL_DATA, "cert_check_confirm", "支付宝安全验证,点击安全验证对话框的“确定”按钮"),
    ECOMMERCE_CRAWL_EXIT("30100402", Step.CRAWL_DATA, "page_exit", "退出当前页面。点击左上角“<\"按钮"),
    ECOMMERCE_CRAWL_EXIT_CONFIRM("30100403", Step.CRAWL_DATA, "exit_confirm", "中断电商导入并退出当前页。退出对话框中，点击“确定退出”按钮"),
    ECOMMERCE_CRAWL_QRCODE_NEXT("30100404", Step.CRAWL_DATA, "qrcode_next_click", "支付宝授权环节点击“已截图，下一步”按钮"),
    /**
     * 完成页
     */
    ECOMMERCE_SUCCESS_CONFIRM("30100501", Step.COMPLETE, "success_confirm", "点击“导入成功”按钮"),
    ECOMMERCE_COMPLETE_PAGE_EXIT("30100502", Step.COMPLETE, "page_exit", "退出当前页面。点击左上角“<\"按钮"),
    /**
     * 导入失败页
     */
    ECOMMERCE_FAILURE_RETRY("30100701", Step.FAILURE, "failure_retry", "点击“重新导入”"),
    /**
     * 系统操作
     */
    ECOMMERCE_TAOBAO_LOGIN_START("30900101", Step.LOGIN, "start", "电商开始登录"),
    ECOMMERCE_TAOBAO_LOGIN_SMS_START("30900102", Step.LOGIN, "sms_start", "触发短信验证"),
    ECOMMERCE_TAOBAO_LOGIN_SMS_RECEIVED("30900103", Step.LOGIN, "sms_received", "收到短信验证码"),
    ECOMMERCE_TAOBAO_LOGIN_SMS_SUCCESS("30900104", Step.LOGIN, "sms_success", "短信验证成功"),
    ECOMMERCE_TAOBAO_LOGIN_SUCCESS("30900190", Step.LOGIN, "success", "电商登录完成"),
    ECOMMERCE_CRAWL_PRE("30900200", Step.CRAWL_DATA, "prepare", "收到消息，准备爬虫"),
    ECOMMERCE_CRAWL_START("30900201", Step.CRAWL_DATA, "start", "启动爬虫，开始爬取"),
    ECOMMERCE_CRAWL_SMS_START("30900202", Step.CRAWL_DATA, "sms_start", "触发短信验证"),
    ECOMMERCE_CRAWL_SMS_RECEIVED("30900203", Step.CRAWL_DATA, "sms_received", "收到短信验证码"),
    ECOMMERCE_CRAWL_SMS_SUCCESS("30900204", Step.CRAWL_DATA, "sms_success", "短信验证成功"),
    ECOMMERCE_CRAWL_SUCCESS("30900290", Step.CRAWL_DATA, "success", "爬取成功"),
    ECOMMERCE_DATA_START("30900301", Step.PROCESS_DATA, "start", "数据开始清洗"),
    ECOMMERCE_DATA_SUCCESS("30900302", Step.PROCESS_DATA, "success", "数据入库"),
    ECOMMERCE_DATA_SNAPSHOT("30900303", Step.PROCESS_DATA, "snapshot", "生成回调数据快照"),
    ECOMMERCE_CALLBACK_START("30900401", Step.CALLBACK, "start", "回调通知开始"),
    ECOMMERCE_CALLBACK_SUCCESS("30900402", Step.CALLBACK, "success", "回调通知成功");

    private final String code;
    private final String step;
    private final String subStep;
    private final String msg;

    CodeStepEnum(String code, String step, String subStep, String msg) {
        this.code = code;
        this.step = step;
        this.subStep = subStep;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getStep() {
        return step;
    }

    public String getSubStep() {
        return subStep;
    }

    public String getMsg() {
        return msg;
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

    public static String createSystemTaskPointCode(Integer bizType, String subcode) {
        return BizTypeFlag.createSystemTaskPointCode(bizType, subcode);
    }

    private static class BizTypeFlag {
        private static final Map<Integer, String> BIZ_TYPE_MAP = new HashMap<>();
        static {
            BIZ_TYPE_MAP.put(1, "20");
            BIZ_TYPE_MAP.put(2, "30");
            BIZ_TYPE_MAP.put(3, "10");
        }

        static String createSystemTaskPointCode(Integer bizType, String subcode) {
            String prefix = BIZ_TYPE_MAP.getOrDefault(bizType, "00");
            return prefix + subcode;
        }
    }

    private static class Step {
        private static final String PRE_LOGIN = "pre_login";
        private static final String LOGIN = "login";

        private static final String CRAWL_DATA = "crawl_data";
        private static final String PROCESS_DATA = "process_data";
        private static final String CALLBACK = "callback";

        private static final String COMPLETE = "complete";
        private static final String FAILURE = "failure";
        private static final String OTHER = "other";
    }
}
