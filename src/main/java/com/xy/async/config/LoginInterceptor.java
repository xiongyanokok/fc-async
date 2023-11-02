package com.xy.async.config;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.xy.async.constant.AsyncConstant;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 统一登录拦截器
 *
 * @author xiongyan
 * @date 2022/8/17
 */
@Slf4j
@Aspect
@Component
@ConditionalOnProperty(prefix = "async", value = "enabled", havingValue = "true")
public class LoginInterceptor {

    @Value("${async.login.enabled:false}")
    private boolean loginEnabled;

    @Value("${async.login.url:}")
    private String loginUrl;

    @Value("${spring.application.name}")
    private String applicationName;

    private static final String RESULT = "{\"code\":%s,\"msg\":\"%s\"}";

    @Pointcut("within(com.xy.async.controller.AsyncController)")
    public void pointcut() {
    }

    @Around(value = "pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!loginEnabled) {
            return joinPoint.proceed();
        }

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        try {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            StringBuilder url = new StringBuilder("登录URL?redirect=");
            url.append("应用URL");
            url.append(applicationName);
            url.append("/async/index.html");
            String authorization = request.getHeader("authorization");
            if (StrUtil.isEmpty(authorization) || !this.login(authorization)) {
                this.print(servletRequestAttributes, AsyncConstant.LOGIN, url.toString());
                return null;
            }
            return joinPoint.proceed();
        } catch (Exception e) {
            log.error("统一认证处理失败", e);
            this.print(servletRequestAttributes, AsyncConstant.ERROR, "请联系管理员");
            return null;
        }
    }

    /**
     * 根据token校验是否登录
     * 
     * @param authorization
     * @return
     */
    private boolean login(String authorization) {
        HttpRequest httpRequest = HttpUtil.createPost(loginUrl);
        httpRequest.header("authorization", authorization);
        HttpResponse response = httpRequest.execute();
        String result = response.body();
        if (StrUtil.isEmpty(result)) {
            return false;
        }
        return JSONUtil.parseObj(result).getBool("success");
    }

    /**
     * 失败响应
     * 
     * @param servletRequestAttributes
     * @param code
     * @param msg
     * @throws IOException
     */
    private void print(ServletRequestAttributes servletRequestAttributes, Integer code, String msg) throws IOException {
        servletRequestAttributes.getResponse().getWriter().print(String.format(RESULT, code, msg));
    }

}
