package com.selfimpr.captcha.service;

import com.selfimpr.captcha.exception.ServiceException;
import com.selfimpr.captcha.model.dto.ImageVerificationDto;
import com.selfimpr.captcha.model.vo.ImageVerificationVo;

/**
 * description:  验证码业务处理类
 * version:  1.0
 * date: 2019/08/23 17:16
 * @author: YangXingfu
 */
public interface CaptchaService {

    /**
     * 根据类型获取验证码
     * @param imageVerificationDto  图片类型dto
     * @return  图片验证码
     * @throws ServiceException 获取图片验证码异常
     */
    ImageVerificationVo selectImageVerificationCode(ImageVerificationDto imageVerificationDto) throws ServiceException;

    /**
     * 校验图片验证码
     * @param x x轴坐标
     * @param y y轴坐标
     * @return 校验结果
     * @throws ServiceException 校验图片验证码异常
     */
    boolean checkVerificationResult(String x, String y) throws ServiceException;
}
