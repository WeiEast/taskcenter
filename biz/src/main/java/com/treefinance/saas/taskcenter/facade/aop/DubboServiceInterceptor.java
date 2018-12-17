package com.treefinance.saas.taskcenter.facade.aop;

import com.alibaba.fastjson.JSONArray;
import com.google.common.base.Stopwatch;
import com.treefinance.saas.taskcenter.exception.BusinessCheckFailException;
import com.treefinance.saas.taskcenter.exception.BusinessProcessFailException;
import com.treefinance.saas.taskcenter.exception.UnexpectedServiceException;
import com.treefinance.saas.taskcenter.facade.response.TaskResponse;
import com.treefinance.saas.taskcenter.facade.result.common.TaskPagingResult;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.toolkit.util.json.Jackson;
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
    public Object triggerAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Object result = null;
        try {
            result = joinPoint.proceed();
            return result;
        } catch (UnexpectedServiceException e) {
            result = triggerAfterException(e, joinPoint);
            return result;
        } catch (BusinessCheckFailException e) {
            log.error("BusinessCheckFailException", e);
            result = exceptionProcessor(joinPoint, e);
            return result;
        } catch (BusinessProcessFailException e) {
            log.error("BusinessProcessFailException", e);
            result = exceptionProcessor(joinPoint, e);
            return result;
        } catch (Exception e) {
            log.error("Exception:", e);
            result = exceptionProcessor(joinPoint, e);
            return result;
        } finally {
            triggerAfterCompletion(result, joinPoint, stopwatch);
        }
    }

    private Object triggerAfterException(Exception e, ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = method.getDeclaringClass().getName() + "." + method.getName();

        if (e instanceof UnexpectedServiceException) {
            String errorCode = ((UnexpectedServiceException)e).getErrorCode();
            String errorMsg = e.getMessage();
            log.error("RPC服务异常！服务名: {}, 参数: {}, errorCode: {}, errorMsg: {}", methodName, Jackson.toJSONString(joinPoint.getArgs()), errorCode, errorMsg, e);
            return TaskResponse.failure(errorCode, errorMsg);
        } else {
            // TODO: 李梁杰 2018/12/13 后面逐渐完善异常
            return null;
        }
    }

    private void triggerAfterCompletion(Object result, ProceedingJoinPoint joinPoint, Stopwatch stopwatch) {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = method.getDeclaringClass().getName() + "." + method.getName();
        log.info("RPC服务响应，服务名: {}, 参数: {}, 耗时: {}, 返回结果: {}", methodName, Jackson.toJSONString(joinPoint.getArgs()), stopwatch.toString(), Jackson.toJSONString(result));
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
