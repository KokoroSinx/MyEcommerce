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

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    private static final Map<Class<?>, SetterBundle> CACHE = new ConcurrentHashMap<>();

    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {}

    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();

        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) return;

        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        for (Object arg : args) {
            fillArg(arg, operationType, now, currentId);
        }
    }

    private void fillArg(Object arg, OperationType op, LocalDateTime now, Long currentId) {
        if (arg == null) return;

        // List / Set etc.
        if (arg instanceof Collection<?> col) {
            for (Object item : col) fillEntity(item, op, now, currentId);
            return;
        }

        // MyBatis @Param often becomes a Map
        if (arg instanceof Map<?, ?> map) {
            for (Object v : map.values()) {
                // avoid filling simple types like Long/String
                fillEntity(v, op, now, currentId);
            }
            return;
        }

        fillEntity(arg, op, now, currentId);
    }

    private void fillEntity(Object entity, OperationType op, LocalDateTime now, Long currentId) {
        if (entity == null) return;

        // Skip simple types (Long, String, Integer...)
        if (isSimpleValueType(entity.getClass())) return;

        SetterBundle setters = CACHE.computeIfAbsent(entity.getClass(), AutoFillAspect::resolveSetters);

        try {
            if (op == OperationType.INSERT) {
                setters.setCreateTime(entity, now);
                setters.setCreateUser(entity, currentId);
                setters.setUpdateTime(entity, now);
                setters.setUpdateUser(entity, currentId);
            } else if (op == OperationType.UPDATE) {
                setters.setUpdateTime(entity, now);
                setters.setUpdateUser(entity, currentId);
            }
        } catch (Exception e) {
            log.warn("AutoFill failed for entityClass={}, op={}", entity.getClass().getName(), op, e);
        }
    }

    private static SetterBundle resolveSetters(Class<?> clazz) {
        return new SetterBundle(
                findSetter(clazz, AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class),
                findSetter(clazz, AutoFillConstant.SET_CREATE_USER, Long.class),
                findSetter(clazz, AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class),
                findSetter(clazz, AutoFillConstant.SET_UPDATE_USER, Long.class)
        );
    }

    private static Method findSetter(Class<?> clazz, String name, Class<?> paramType) {
        try {
            return clazz.getMethod(name, paramType); // includes inherited methods
        } catch (NoSuchMethodException e) {
            return null; // ok: entity might not have that field
        }
    }

    private static boolean isSimpleValueType(Class<?> clazz) {
        return clazz.isPrimitive()
                || Number.class.isAssignableFrom(clazz)
                || CharSequence.class.isAssignableFrom(clazz)
                || Date.class.isAssignableFrom(clazz)
                || UUID.class.isAssignableFrom(clazz)
                || Boolean.class == clazz
                || Character.class == clazz;
    }

    private record SetterBundle(Method setCreateTime, Method setCreateUser,
                                Method setUpdateTime, Method setUpdateUser) {

        void setCreateTime(Object target, LocalDateTime now) throws Exception {
            if (setCreateTime != null) setCreateTime.invoke(target, now);
        }

        void setCreateUser(Object target, Long id) throws Exception {
            if (setCreateUser != null) setCreateUser.invoke(target, id);
        }

        void setUpdateTime(Object target, LocalDateTime now) throws Exception {
            if (setUpdateTime != null) setUpdateTime.invoke(target, now);
        }

        void setUpdateUser(Object target, Long id) throws Exception {
            if (setUpdateUser != null) setUpdateUser.invoke(target, id);
        }
    }
}
