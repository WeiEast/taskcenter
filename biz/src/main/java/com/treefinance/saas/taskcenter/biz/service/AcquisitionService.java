package com.treefinance.saas.taskcenter.biz.service;

import com.datatrees.common.util.GsonUtils;
import com.google.gson.reflect.TypeToken;
import com.treefinance.saas.taskcenter.biz.mq.MessageProducer;
import com.treefinance.saas.taskcenter.biz.mq.model.LoginMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * Created by luoyihua on 2017/5/10.
 */
@Service
public class AcquisitionService {
    private static final Logger logger = LoggerFactory.getLogger(AcquisitionService.class);

    @Autowired
    private MessageProducer messageProducer;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskTimeService taskTimeService;

    public void acquisition(Long taskid, String header, String cookie, String url, String website, String accountNo, String topic) {
        logger.info("acquisition : taskid={},header={},cookie={},url={},website={},accountNo={}", taskid, header, cookie, url, website, accountNo);
        LoginMessage loginMessage = new LoginMessage();
        loginMessage.setCookie(cookie);
        loginMessage.setEndUrl(url);
        loginMessage.setTaskId(taskid);
        loginMessage.setWebsiteName(website);
        loginMessage.setAccountNo(accountNo);
        if (StringUtils.isNotEmpty(header)) {
            Map map = (Map) GsonUtils.fromJson(header, new TypeToken<Map>() {
            }.getType());
            if (map.get("Set-Cookie") != null) {
                loginMessage.setSetCookie((String) map.get("Set-Cookie"));
            }
        }
        try {
            messageProducer.send(GsonUtils.toJson(loginMessage), topic, "login_info", taskid.toString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        if (StringUtils.isNotEmpty(accountNo)) {
            taskService.setAccountNo(taskid, accountNo);
        }
        if (StringUtils.isNotEmpty(website)) {
            taskService.updateWebSite(taskid, website);
        }
        taskTimeService.updateLoginTime(taskid, new Date());
    }

    @Deprecated
    //兴海:这个已经不用了
    public void loginProcess(String directiveId, Long taskid, String html, String cookie) {
//        HttpResult<Boolean> res = crawlerService.importAppCrawlResult(directiveId, taskid, html, cookie, null);
//        taskNextDirectiveService.deleteNextDirective(taskid, EDirective.GRAB_URL.getText());
//        logger.debug("taskId={}已发送sdk爬取结果={}", taskid, JSON.toJSONString(res));
    }
}