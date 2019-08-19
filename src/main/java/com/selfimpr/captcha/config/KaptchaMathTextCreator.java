package com.selfimpr.captcha.config;


import com.google.code.kaptcha.text.impl.DefaultTextCreator;

import java.util.Random;

/**
 * @Description: 运算验证码
 * -------------------
 * @Author: YangXingfu
 * @Date: 2019/07/12 09:11
 */

public class KaptchaMathTextCreator extends DefaultTextCreator {

    StringBuilder result = new StringBuilder();


    @Override
    public String getText() {
        Random random = new Random(System.currentTimeMillis());
        int x = random.nextInt(51);
        int y = random.nextInt(51);
        int operationalRules = random.nextInt(4);

        switch (operationalRules) {
            case 0 : add(x, y); break;
            case 1 : subtract(x, y); break;
            case 2 : multiply(x, y); break;
            case 3 : divide(x, y); break;
        }
        return result.toString();
    }
    
    private void add(int x, int y) {
        result.append(x);
        result.append(" + ");
        result.append(y);
        result.append(" = ?@");
        result.append(x + y);
    }

    private void subtract(int x, int y) {
        int max = Math.max(x, y);
        int min = Math.min(x, y);
        result.append(max);
        result.append(" - ");
        result.append(min);
        result.append(" = ?@");
        result.append(max - min);
    }

    private void multiply(int x, int y) {
        int value = x * y;
        result.append(x);
        result.append(value > 100 ? " + " : " * ");
        result.append(y);
        result.append(" = ?@");
        result.append(value > 100 ? x + y : x * y);
    }

    private void divide(int x, int y) {
        int max = Math.max(x, y);
        int min = Math.min(x, y);
        if (min == 0) {
            multiply(max, min);
        } else if(max % min == 0) {
            result.append(max);
            result.append(" / ");
            result.append(min);
            result.append(" = ?@");
            result.append(max / min);
        } else {
            result.append(max);
            result.append(" % ");
            result.append(min);
            result.append(" = ?@");
            result.append(max % min);
        }
    }

}