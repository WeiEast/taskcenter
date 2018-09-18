package com.treefinance.saas.taskcenter.biz.facade.impl;

import com.treefinance.saas.taskcenter.common.exception.BusinessCheckFailException;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskFacade;
import org.springframework.stereotype.Service;

/**
 * @author:guoguoyun
 * @date:Created in 2018/9/18上午10:24
 */
@Service("taskFacade")
public class TaskFacadeImpl implements TaskFacade {

    @Override
    public TaskResult<Object> testAop(String a, String b) {
        if ("hao".equals(a)) {
            throw new BusinessCheckFailException("-1", "参数异常");
        }
        System.out.println("a=" + a);
        return TaskResult.wrapSuccessfulResult(a);
    }

}
