package com.treefinance.saas.taskcenter.facade.service;

import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;

/**
 * @author:guoguoyun
 * @date:Created in 2018/9/18上午10:20
 */
public interface TaskFacade {

    TaskResult<Object> testAop(String a, String b);
}
