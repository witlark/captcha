package com.selfimpr.captcha.controller;


import com.selfimpr.captcha.exception.ServiceException;
import com.selfimpr.captcha.model.dto.ImageVerificationDto;
import com.selfimpr.captcha.model.vo.ImageVerificationVo;
import com.selfimpr.captcha.service.CaptchaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Description: 滑动验证码
 * -------------------
 * @Author: YangXingfu
 * @Date: 2019/07/24 17:01
 */

@Controller
@RequestMapping("/captcha")
public class CaptchaController {

    /**
     * 日志
     */
    private static final Logger log = LoggerFactory.getLogger(CaptchaController.class);

    /**
     * 运算码业务处理对象
     */
    @Autowired
    private CaptchaService captchaService;

    /**
     * 调用内部freemarker模板
     * @return 跳转index.ftl
     */
    @RequestMapping("/index")
    public String index() {
        return "indexs";
    }

    /**
     * 获取验证码
     * @param imageVerificationDto 验证码参数
     * @return 根据类型参数返回验证码
     */
    @RequestMapping("/get/verification/image")
    @ResponseBody
    public ImageVerificationVo getVerificationImage(ImageVerificationDto imageVerificationDto) {
        ImageVerificationVo imageVerificationVo = null;
        try {
            imageVerificationVo = captchaService.selectImageVerificationCode(imageVerificationDto);
        } catch (ServiceException e) {
            log.error(e.getCode(), e.getMsg());
            return null;
        }
        return imageVerificationVo;
    }

    /**
     * 校验验证码
     * @param x x轴坐标
     * @param y y轴坐标
     * @return 验证结果
     */
    @ResponseBody
    @RequestMapping("/check/verification/result")
    public boolean checkVerificationResult(String x, String y) {
        boolean result = false;
        try {
            result = captchaService.checkVerificationResult(x, y);
        } catch (ServiceException e) {
            log.error(e.getCode(), e.getMsg());
            return false;
        }
        return result;
    }
}