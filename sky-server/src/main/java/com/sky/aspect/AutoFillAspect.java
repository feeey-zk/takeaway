package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面，实现公共字段自动填充
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /**
     * 切入点定义
     */
    //切点表达式
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {

    }

    /**
     * 前置通知，在目标方法执行前执行；在通知中为公共字段赋值
     *
     * @param joinPoint
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("开始进行公共字段自动填充...");
        //获取被拦截方法的操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature(); //获取方法签名
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class); //获取方法上的注解
        OperationType value = annotation.value(); //获取数据库操作类型
        //获取被拦截方法的参数
        Object[] args = joinPoint.getArgs(); //获取方法参数
        if (args == null || args.length == 0) {
            return;
        }
        Object arg = args[0]; //获取第一个参数

        //准备公共字段数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //根据不同的操作类型，为对应的实体对象赋值----通过反射
        if (value == OperationType.INSERT) {
            try {
                Method setCreateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                //通过反射为公共字段赋值
                setCreateTime.invoke(arg, now);
                setCreateUser.invoke(arg, currentId);
                setUpdateTime.invoke(arg, now);
                setUpdateUser.invoke(arg, currentId);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } else if (value == OperationType.UPDATE) {
            try {
                Method setUpdateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                //通过反射为公共字段赋值
                setUpdateTime.invoke(arg, now);
                setUpdateUser.invoke(arg, currentId);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
