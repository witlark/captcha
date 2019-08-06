package com.selfimpr.captcha.service;

import com.selfimpr.captcha.exception.ServiceException;

import java.util.Map;

public interface CaptchaService {
    Map<String, String> getVerificationImage() throws ServiceException;

    boolean checkVerificationResult(String x, String y) throws ServiceException;
}
