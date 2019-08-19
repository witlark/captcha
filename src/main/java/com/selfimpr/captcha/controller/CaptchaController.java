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

    @ResponseBody
    @RequestMapping("/check/verification/result")
    public boolean checkVerificationResult(String X, String Y) {
        boolean result = false;
        try {
            result = captchaService.checkVerificationResult(X, Y);
        } catch (ServiceException e) {
            log.error(e.getCode(), e.getMsg());
            return false;
        }
        return result;
    }
}