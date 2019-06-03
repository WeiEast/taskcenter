/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.treefinance.saas.taskcenter.common.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * 任务详细环节对应编码为：
 * <p>
 * Created by haojiahong on 2017/8/21.
 */
public enum ETaskStep {

    /**
     * 任务创建
     */
    TASK_CREATE("任务创建", "CJ", "创建", "CJ01", "任务创建"),
    /**
     * 等待用户扫描二维码
     */
    WAITING_USER_SCAN_QR_CODE("等待用户扫描二维码", "PS", "爬数", "PS01", "扫描二维码"),
    /**
     * 二维码验证失败
     */
    QR_CODE_VALIDATE_FAIL("二维码验证失败", "PS", "爬数", "PS01", "扫描二维码"),
    /**
     * 二维码验证成功
     */
    QR_CODE_VALIDATE_SUCCESS("二维码验证成功", "PS", "爬数", "PS01", "扫描二维码"),
    /**
     * 等待用户输入短信验证码
     */
    WAITING_USER_INPUT_MESSAGE_CODE("等待用户输入短信验证码", "PS", "爬数", "PS02", "短信验证码"),
    /**
     * 向手机发送短信验证码
     */
    SENDING_MESSAGE_CODE("向手机发送短信验证码", "PS", "爬数", "PS02", "短信验证码"),
    /**
     * 短信验证码校验失败
     */
    MESSAGE_CODE_VALIDATE_FAIL("短信验证码校验失败", "PS", "爬数", "PS02", "短信验证码"),
    /**
     * 短信验证码校验成功
     */
    MESSAGE_CODE_VALIDATE_SUCCESS("短信验证码校验成功", "PS", "爬数", "PS02", "短信验证码"),
    /**
     * 短信验证码校验超时
     */
    MESSAGE_CODE_VALIDATE_TIMEOUT("短信验证码校验超时", "PS", "爬数", "PS02", "短信验证码"),
    /**
     * 等待用户输入图片验证码
     */
    WAITING_USER_INPUT_IMAGE_CODE("等待用户输入图片验证码", "PS", "爬数", "PS03", "图片验证码"),
    /**
     * 图片验证码校验成功
     */
    IMAGE_CODE_VALIDATE_SUCCESS("图片验证码校验成功", "PS", "爬数", "PS03", "图片验证码"),
    /**
     * 图片验证码校验超时
     */
    IMAGE_CODE_VALIDATE_TIMEOUT("图片验证码校验超时", "PS", "爬数", "PS03", "图片验证码"),
    /**
     * 刷新图片验证码
     */
    REFRESHING_IMAGE_CODE("刷新图片验证码", "PS", "爬数", "PS03", "图片验证码"),
    /**
     * 登陆失败
     */
    LOGIN_FAIL("登陆失败", "PS", "爬数", "PS04", "用户登陆"),
    /**
     * 登陆成功
     */
    LOGIN_SUCCESS("登陆成功", "PS", "爬数", "PS04", "用户登陆"),
    /**
     * 开始抓取
     */
    START_CRAWL("开始抓取", "PS", "爬数", "PS05", "数据抓取"),
    /**
     * 抓取失败
     */
    CRAWL_FAIL("抓取失败", "PS", "爬数", "PS05", "数据抓取"),
    /**
     * 抓取成功
     */
    CRAWL_SUCCESS("抓取成功", "PS", "爬数", "PS05", "数据抓取"),
    /**
     * 爬数任务执行完成
     */
    CRAWL_COMPLETE("爬数任务执行完成", "PS", "爬数", "PS05", "数据抓取"),
    /**
     * 抓取中断
     */
    CRAWL_INTERRUPT("抓取中断", "PS", "爬数", "PS05", "数据抓取"),
    /**
     * 用户刷新任务或者重试,抓取中断
     */
    USER_REFRESH_RETRY_CRAWL_INTERRUPT("用户刷新任务或者重试,抓取中断", "PS", "爬数", "PS06", "用户刷新"),
    /**
     * 数据预处理失败
     */
    DATA_PREPROCESSED_FAIL("数据预处理失败", "XS", "洗数", "XS01", "数据预处理"),
    /**
     * 数据预处理成功
     */
    DATA_PREPROCESSED_SUCCESS("数据预处理成功", "XS", "洗数", "XS01", "数据预处理"),
    /**
     * 数据处理失败
     */
    DATA_PROCESS_FAIL("数据处理失败", "XS", "洗数", "XS02", "数据处理"),
    /**
     * 数据处理成功
     */
    DATA_PROCESS_SUCCESS("数据处理成功", "XS", "洗数", "XS02", "数据处理"),
    /**
     * 数据清洗成功
     */
    DATA_CLEAN_SUCCESS("数据清洗成功", "XS", "洗数", "XS03", "数据清洗"),
    /**
     * 数据保存失败
     */
    DATA_SAVE_FAIL("数据保存失败", "XS", "洗数", "XS04", "数据保存"),
    /**
     * 数据保存成功
     */
    DATA_SAVE_SUCCESS("数据保存成功", "XS", "洗数", "XS04", "数据保存"),
    /**
     * 回调通知失败
     */
    CALLBACK_FAIL("回调通知失败", "HD", "回调", "HD01", "回调通知"),
    /**
     * 回调通知成功
     */
    CALLBACK_SUCCESS("回调通知成功", "HD", "回调", "HD01", "回调通知"),
    /**
     * 任务成功
     */
    TASK_SUCCESS("任务成功", "WC", "完成", "WC01", "任务完成"),
    /**
     * 任务失败
     */
    TASK_FAIL("任务失败", "WC", "完成", "WC01", "任务完成"),
    /**
     * 任务取消
     */
    TASK_CANCEL("任务取消", "WC", "完成", "WC01", "任务完成"),
    /**
     * 任务超时
     */
    TASK_TIMEOUT("任务超时", "WC", "完成", "WC01", "任务完成");

    /**
     * 指令日志信息
     */
    private final String text;
    /**
     * 阶段编码
     */
    private final String stageCode;
    /**
     * 阶段信息
     */
    private final String stageText;
    /**
     * 环节编码
     */
    private final String stepCode;
    /**
     * 环节信息
     */
    private final String stepText;

    ETaskStep(String text, String stageCode, String stageText, String stepCode, String stepText) {
        this.text = text;
        this.stageCode = stageCode;
        this.stageText = stageText;
        this.stepCode = stepCode;
        this.stepText = stepText;
    }

    public String getText() {
        return text;
    }

    public String getStageCode() {
        return stageCode;
    }

    public String getStageText() {
        return stageText;
    }

    public String getStepCode() {
        return stepCode;
    }

    public String getStepText() {
        return stepText;
    }

    /**
     * 根据<code>text</code>获取{@link ETaskStep};
     */
    public static ETaskStep fromText(String text) {
        if (StringUtils.isNotEmpty(text)) {
            return Arrays.stream(ETaskStep.values()).filter(item -> item.getText().equals(text)).findFirst().orElse(null);
        }
        return null;
    }

    /**
     * 根据<code>stepCode</code>获取{@link ETaskStep};
     */
    public static ETaskStep fromStepCode(String stepCode) {
        if (StringUtils.isNotEmpty(stepCode)) {
            return Arrays.stream(ETaskStep.values()).filter(item -> item.getStepCode().equals(stepCode)).findFirst().orElse(null);
        }
        return null;
    }

    /**
     * 根据<code>text</code>获取对应的{@link ETaskStep#getStepCode()};
     */
    public static String getStepCodeByText(String text) {
        ETaskStep step = fromText(text);

        return step != null ? step.getStepCode() : null;
    }

    /**
     * 根据<code>stepCode</code>获取对应的{@link ETaskStep#getStageCode()};
     */
    public static String getStageCodeByStepCode(String stepCode) {
        ETaskStep step = fromStepCode(stepCode);

        return step != null ? step.getStageCode() : null;
    }

    /**
     * 根据<code>stepCode</code>获取对应的{@link ETaskStep#getStageText()};
     */
    public static String getStageTextByStepCode(String stepCode) {
        ETaskStep step = fromStepCode(stepCode);

        return step != null ? step.getStageText() : null;
    }
}
