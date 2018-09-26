package com.treefinance.saas.taskcenter.biz.service;

import com.datatrees.spider.share.api.SpiderTaskApi;
import com.treefinance.basicservice.security.crypto.facade.EncryptionIntensityEnum;
import com.treefinance.basicservice.security.crypto.facade.ISecurityCryptoService;
import com.treefinance.saas.taskcenter.common.model.dto.TaskDTO;
import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.dao.mapper.TaskMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 记录二维码登录爬数回传的账号信息
 * Created by haojiahong on 2018/1/18.
 */
@Service
public class QRCodeAccountNoLogService {

    private final static Logger logger = LoggerFactory.getLogger(QRCodeAccountNoLogService.class);

    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private SpiderTaskApi spiderTaskApi;
    @Autowired
    private ISecurityCryptoService securityCryptoService;

    public void logQRCodeAccountNo(Long taskId) {
        TaskDTO taskDTO = taskService.getById(taskId);
        if (taskDTO != null && StringUtils.isNotBlank(taskDTO.getAccountNo())) {
            return;
        }
        String accountNo = null;
        try {
            accountNo = spiderTaskApi.getTaskAccountNo(taskId);
            logger.info("记录任务accountNo:调用爬数查询任务账号信息,taskId={},accountNo={}", taskId, accountNo);
        } catch (Exception e) {
            logger.error("记录任务accountNo:调用爬数查询任务账号信息异常,taskId={}", taskId, e);
        }
        if (StringUtils.isNotBlank(accountNo)) {
            logger.info("记录任务accountNo:taskId={},accountNo={}", taskId, accountNo);
            Task task = new Task();
            task.setId(taskId);
            String cryptoAccountNo = securityCryptoService.encrypt(accountNo, EncryptionIntensityEnum.NORMAL);
            task.setAccountNo(cryptoAccountNo);
            taskMapper.updateByPrimaryKeySelective(task);
        }
    }
}
