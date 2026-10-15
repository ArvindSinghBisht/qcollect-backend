package com.example.qcollect.common.web;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Trims request emails after Jackson binds the body and before {@code @Valid} runs.
 * Letter case is left unchanged.
 */
@ControllerAdvice
public class EmailTrimRequestBodyAdvice implements RequestBodyAdvice {

    @Override
    public boolean supports(
            MethodParameter methodParameter,
            Type targetType,
            Class<? extends HttpMessageConverter<?>> converterType
    ) {
        if (hasEmailAccessors(rawClass(targetType))) {
            return true;
        }
        if (targetType instanceof java.lang.reflect.ParameterizedType parameterized) {
            for (Type argument : parameterized.getActualTypeArguments()) {
                if (hasEmailAccessors(rawClass(argument))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public HttpInputMessage beforeBodyRead(
            HttpInputMessage inputMessage,
            MethodParameter parameter,
            Type targetType,
            Class<? extends HttpMessageConverter<?>> converterType
    ) {
        return inputMessage;
    }

    @Override
    public Object afterBodyRead(
            Object body,
            HttpInputMessage inputMessage,
            MethodParameter parameter,
            Type targetType,
            Class<? extends HttpMessageConverter<?>> converterType
    ) {
        trimEmails(body);
        return body;
    }

    @Override
    public Object handleEmptyBody(
            Object body,
            HttpInputMessage inputMessage,
            MethodParameter parameter,
            Type targetType,
            Class<? extends HttpMessageConverter<?>> converterType
    ) {
        return body;
    }

    private static void trimEmails(Object body) {
        if (body instanceof Iterable<?> items) {
            for (Object item : items) {
                trimOne(item);
            }
            return;
        }
        trimOne(body);
    }

    private static void trimOne(Object body) {
        if (body == null || !hasEmailAccessors(body.getClass())) {
            return;
        }
        try {
            Method getter = body.getClass().getMethod("getEmail");
            Method setter = body.getClass().getMethod("setEmail", String.class);
            Object value = getter.invoke(body);
            if (value instanceof String email) {
                setter.invoke(body, email.trim());
            }
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Could not trim request email.", ex);
        }
    }

    private static boolean hasEmailAccessors(Class<?> type) {
        if (type == null) {
            return false;
        }
        try {
            type.getMethod("getEmail");
            type.getMethod("setEmail", String.class);
            return true;
        } catch (NoSuchMethodException ex) {
            return false;
        }
    }

    private static Class<?> rawClass(Type type) {
        if (type instanceof Class<?> clazz) {
            return clazz;
        }
        if (type instanceof java.lang.reflect.ParameterizedType parameterized
                && parameterized.getRawType() instanceof Class<?> raw) {
            return raw;
        }
        return null;
    }
}
