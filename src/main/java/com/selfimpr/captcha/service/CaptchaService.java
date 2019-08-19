package com.selfimpr.captcha.service;

import com.selfimpr.captcha.exception.ServiceException;
import com.selfimpr.captcha.model.dto.ImageVerificationDto;
import com.selfimpr.captcha.model.vo.ImageVerificationVo;

public interface CaptchaService {
    ImageVerificationVo selectImageVerificationCode(ImageVerificationDto imageVerificationDto) throws ServiceException;

    boolean checkVerificationResult(String x, String y) throws ServiceException;
}
