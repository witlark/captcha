package com.selfimpr.captcha.model.vo;


import java.io.Serializable;

/**
 * @Description: 封装图片验证码
 * -------------------
 * @Author: YangXingfu
 * @Date: 2019/07/29 16:18
 */

public class ImageVerificationVo implements Serializable {

    /**
     * 验证码类型
     */
    private String type;

    /**
     * 字符验证码
     */
    private String charImage;

    /**
     * 加减乘除验证码
     */
    private String operationImage;

    /**
     * 滑动验证码，源图
     */
    private String originImage;

    /**
     * 滑动验证码，遮罩图
     */
    private String shadeImage;

    /**
     * 滑动验证码，裁剪图
     */
    private String cutoutImage;

    /**
     * 滑动验证码，X轴
     */
    private int X;

    /**
     * 滑动验证码，Y轴
     */
    private int Y;

    public int getX() {
        return X;
    }

    public void setX(int x) {
        X = x;
    }

    public int getY() {
        return Y;
    }

    public void setY(int y) {
        Y = y;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCharImage() {
        return charImage;
    }

    public void setCharImage(String charImage) {
        this.charImage = charImage;
    }

    public String getOperationImage() {
        return operationImage;
    }

    public void setOperationImage(String operationImage) {
        this.operationImage = operationImage;
    }

    public String getOriginImage() {
        return originImage;
    }

    public void setOriginImage(String originImage) {
        this.originImage = originImage;
    }

    public String getShadeImage() {
        return shadeImage;
    }

    public void setShadeImage(String shadeImage) {
        this.shadeImage = shadeImage;
    }

    public String getCutoutImage() {
        return cutoutImage;
    }

    public void setCutoutImage(String cutoutImage) {
        this.cutoutImage = cutoutImage;
    }
}