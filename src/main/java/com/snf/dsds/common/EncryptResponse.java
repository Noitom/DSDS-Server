package com.snf.dsds.common;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snf.dsds.bean.RespBean;
import com.snf.dsds.common.annotation.Encrypt;
import com.snf.dsds.common.utils.AESUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import com.snf.dsds.common.config.EncryptProperties;

/**
 * @program: dsds
 * @description: 响应数据加密前置处理器
 * @author: zhouyuj
 * @create: 2021-12-23 09:25
 **/
@EnableConfigurationProperties(EncryptProperties.class)
@ControllerAdvice
@Slf4j
public class EncryptResponse implements ResponseBodyAdvice<RespBean> {

    private ObjectMapper om = new ObjectMapper();
    @Autowired
    EncryptProperties encryptProperties;

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        log.info("进入响应数据加密前置处理器");
        return methodParameter.hasMethodAnnotation(Encrypt.class);
    }

    @Override
    public RespBean beforeBodyWrite(RespBean body, MethodParameter returnType, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        log.info("对响应数据进行加密");
        byte[] keyBytes = encryptProperties.getKey().getBytes();
        try {
            if (body.getMsg()!=null) {
                log.info("响应信息【{}】",body.getMsg());
                body.setMsg(AESUtils.encrypt(body.getMsg().getBytes(),keyBytes));
            }
            if (body.getObj() != null) {
                log.info("响应数据",JSON.toJSONString(body.getObj()));
                body.setObj(AESUtils.encrypt(om.writeValueAsBytes(body.getObj()), keyBytes));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
    }
}
