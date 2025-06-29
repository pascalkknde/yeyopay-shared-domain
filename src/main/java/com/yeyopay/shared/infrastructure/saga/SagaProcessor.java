package com.yeyopay.shared.infrastructure.saga;

import com.yeyopay.shared.infrastructure.saga.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import jakarta.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Processes saga annotations and handles saga orchestration.
 */
@Component
public class SagaProcessor {
    @Autowired
    private ApplicationContext applicationContext;

    private final Map<String, Object> sagaHandlers = new HashMap<>();
    private final Map<String, Method> sagaStartMethods = new HashMap<>();
    private final Map<String, Method> sagaStepMethods = new HashMap<>();
    private final Map<String, Method> sagaCompensationMethods = new HashMap<>();
    private final Map<String, Method> sagaCompleteMethods = new HashMap<>();

    @PostConstruct
    public void initializeSagaHandlers() {
        Map<String, Object> beans = applicationContext.getBeansOfType(Object.class);

        for (Object bean : beans.values()) {
            Class<?> clazz = bean.getClass();

            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(SagaStartHandler.class)) {
                    SagaStartHandler annotation = method.getAnnotation(SagaStartHandler.class);
                    String sagaType = annotation.sagaType().isEmpty() ?
                            clazz.getSimpleName() : annotation.sagaType();
                    sagaStartMethods.put(sagaType, method);
                    sagaHandlers.put(sagaType, bean);
                }

                if (method.isAnnotationPresent(SagaStartHandler.class)) {
                    SagaStepHandler annotation = method.getAnnotation(SagaStepHandler.class);

                    String key = annotation.sagaType() + ":" + annotation.step();
                    sagaStepMethods.put(key, method);
                    sagaHandlers.put(key, bean);
                }

                if (method.isAnnotationPresent(SagaCompensationHandler.class)) {
                    SagaCompensationHandler annotation = method.getAnnotation(SagaCompensationHandler.class);
                    String key = annotation.sagaType() + ":" + annotation.step();
                    sagaCompensationMethods.put(key, method);
                    sagaHandlers.put(key, bean);
                }

                if (method.isAnnotationPresent(SagaCompleteHandler.class)) {
                    SagaCompleteHandler annotation = method.getAnnotation(SagaCompleteHandler.class);
                    String sagaType = annotation.sagaType().isEmpty() ?
                            clazz.getSimpleName() : annotation.sagaType();
                    sagaCompleteMethods.put(sagaType, method);
                    sagaHandlers.put(sagaType + ":complete", bean);
                }
            }
        }
    }

    public Mono<Void> processSagaStart(String sagaType, Object eventData) {
        Method method = sagaStartMethods.get(sagaType);
        Object handler = sagaHandlers.get(sagaType);

        if (method != null && handler != null) {
            return invokeMethod(handler, method, eventData);
        }

        return Mono.empty();
    }

    public Mono<Void> processSagaStep(String sagaType, String step, Object eventData) {
        String key = sagaType + ":" + step;
        Method method = sagaStepMethods.get(key);
        Object handler = sagaHandlers.get(key);

        if (method != null && handler != null) {
            return invokeMethod(handler, method, eventData);
        }

        return Mono.empty();
    }

    public Mono<Void> processSagaCompensation(String sagaType, String step, Object eventData) {
        String key = sagaType + ":" + step;
        Method method = sagaCompensationMethods.get(key);
        Object handler = sagaHandlers.get(key);

        if (method != null && handler != null) {
            return invokeMethod(handler, method, eventData);
        }

        return Mono.empty();
    }

    public Mono<Void> processSagaComplete(String sagaType, Object eventData) {
        Method method = sagaCompleteMethods.get(sagaType);
        Object handler = sagaHandlers.get(sagaType + ":complete");

        if (method != null && handler != null) {
            return invokeMethod(handler, method, eventData);
        }

        return Mono.empty();
    }

    @SuppressWarnings("unchecked")
    private Mono<Void> invokeMethod(Object handler, Method method, Object eventData) {
        try {
            method.setAccessible(true);
            Object result = method.invoke(handler, eventData);

            if (result instanceof Mono) {
                return (Mono<Void>) result;
            } else {
                return Mono.empty();
            }
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Failed to invoke saga method", e));
        }
    }

}


