package com.treefinance.saas.taskcenter.facade.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.treefinance.saas.taskcenter.common.exception.BusinessCheckFailException;
import com.treefinance.saas.taskcenter.common.exception.BusinessProcessFailException;
import com.treefinance.saas.taskcenter.facade.result.common.TaskPagingResult;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class DubboServiceInterceptor {

    private static final Logger log = LoggerFactory.getLogger(DubboServiceInterceptor.class);

    @Pointcut("execution(* com.treefinance.saas.taskcenter.facade.impl..*.*(..))")
    public void facadePointcut() {}

    @Around("facadePointcut()")
    public Object triggerAround(ProceedingJoinPoint jpj) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        try {
            result = jpj.proceed();
            return result;
        } catch (BusinessCheckFailException e) {
            log.error("BusinessCheckFailException", e);
            result = exceptionProcessor(jpj, e);
            return result;
        } catch (BusinessProcessFailException e) {
            log.error("BusinessProcessFailException", e);
            result = exceptionProcessor(jpj, e);
            return result;
        } catch (Exception e) {
            log.error("Exception:", e);
            result = exceptionProcessor(jpj, e);
            return result;
        } finally {
            logProcessor(jpj, result, startTime);
        }
    }

    private void logProcessor(ProceedingJoinPoint jpj, Object result, long startTime) {
        MethodSignature signature = (MethodSignature) jpj.getSignature();
        Method method = signature.getMethod();
        String methodName = method.getDeclaringClass().getName() + "." + method.getName();
        log.info("请求dubbo服务:{},参数:{},用时:{}ms,返回结果:{}", methodName, JSONArray.toJSONString(jpj.getArgs()),
                System.currentTimeMillis() - startTime, JSON.toJSONString(result));
    }

    @SuppressWarnings("rawtypes")
    private Object exceptionProcessor(ProceedingJoinPoint jpj, Exception e) {
        Object[] args = jpj.getArgs();
        MethodSignature signature = (MethodSignature) jpj.getSignature();
        Method method = signature.getMethod();
        String methodName = method.getDeclaringClass().getName() + "." + method.getName();
        log.error("dubbo服务[method=" + methodName + "] params=" + JSONArray.toJSONString(args) + "异常：", e);

        Class<?> clazz = method.getReturnType();
        if (clazz.equals(TaskResult.class)) {
            TaskResult result = new TaskResult();
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            if (e instanceof BusinessCheckFailException) {
                result.setCode(((BusinessCheckFailException) e).getErrorCode());
            } else if (e instanceof BusinessProcessFailException) {
                result.setCode(((BusinessProcessFailException) e).getErrorCode());
            } else {
                result.setCode("-100");
                result.setMessage("系统内部错误");
            }
            return result;
        } else if (clazz.equals(TaskPagingResult.class)) {
            TaskPagingResult result = new TaskPagingResult();
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            if (e instanceof BusinessCheckFailException) {
                result.setCode(((BusinessCheckFailException) e).getErrorCode());
            } else if (e instanceof BusinessProcessFailException) {
                result.setCode(((BusinessProcessFailException) e).getErrorCode());
            } else {
                result.setCode("-100");
                result.setMessage("系统内部错误");
            }
            return result;
        }
        log.error("dubbo拦截器发现服务签名错误method={}, returnType=", methodName, clazz);
        return null;
    }

}
