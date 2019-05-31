package com.treefinance.saas.taskcenter.biz.service.directive.impl;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.taskcenter.biz.service.directive.DirectivePacket;
import com.treefinance.saas.taskcenter.biz.service.directive.DirectiveService;
import com.treefinance.saas.taskcenter.biz.service.directive.MoxieDirectivePacket;
import com.treefinance.saas.taskcenter.biz.service.directive.process.DirectiveContext;
import com.treefinance.saas.taskcenter.biz.service.directive.process.DirectiveProcessor;
import com.treefinance.saas.taskcenter.biz.service.directive.process.DirectiveProcessorFactory;
import com.treefinance.saas.taskcenter.common.enums.EDirective;
import com.treefinance.saas.taskcenter.common.enums.ETaskAttribute;
import com.treefinance.saas.taskcenter.dao.entity.TaskAttribute;
import com.treefinance.saas.taskcenter.interation.manager.LicenseManager;
import com.treefinance.saas.taskcenter.service.TaskAttributeService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DirectiveServiceImpl implements DirectiveService {
    // logger
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TaskAttributeService taskAttributeService;
    @Autowired
    private LicenseManager licenseManager;
    @Autowired
    private DirectiveProcessorFactory directiveProcessorFactory;

    @Override
    public void process(DirectivePacket directivePacket) {
        EDirective directive = directivePacket.getDirective();
        if (directive == null) {
            logger.error("not supported directive : {} ...", JSON.toJSONString(directivePacket));
            return;
        }

        final String alias = directivePacket.getAlias();
        if (EDirective.CUSTOM.equals(directive) && StringUtils.isEmpty(alias)) {
            logger.error("not supported directive : {} ...", JSON.toJSONString(directivePacket));
            return;
        }

        DirectiveContext context = DirectiveContext.create(directive, alias);
        context.setLicenseManager(licenseManager);
        context.setDirectiveId(directivePacket.getDirectiveId());
        context.setTaskId(directivePacket.getTaskId());
        context.setRemark(directivePacket.getRemark());

        if (directivePacket instanceof MoxieDirectivePacket) {
            context.setFromMoxie(true);

            if (context.getTaskId() == null) {
                String moxieTaskId = ((MoxieDirectivePacket) directivePacket).getMoxieTaskId();
                if (StringUtils.isBlank(moxieTaskId)) {
                    logger.warn("processing moxie directive error : Not found moxieTaskId");
                    return;
                }
                TaskAttribute taskAttribute = taskAttributeService.queryAttributeByNameAndValue(ETaskAttribute.FUND_MOXIE_TASKID.getAttribute(), moxieTaskId, false);
                if (taskAttribute == null) {
                    logger.warn("processing moxie directive error : Not found taskId matched with moxieTaskId in task_attribute, moxieTaskId={}", moxieTaskId);
                    return;
                }
                context.setTaskId(taskAttribute.getTaskId());
            }
        }

        DirectiveProcessor directiveProcessor = directiveProcessorFactory.getDirectiveProcessor(context);
        directiveProcessor.process(context);
    }
}
