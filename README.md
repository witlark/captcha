<h1><a href="https://gitee.com/gester/captcha.git">Captcha</a></h1>
<p align="center">
<a href="#"></a><img alt="JDK" src="https://img.shields.io/badge/JDK-1.8-yellow.svg?style=flat-square"/></a>
<a href="https://gitee.com/gester/captcha.git"><img alt="release version" src="https://img.shields.io/badge/release-v1.0.0-blue.svg"></a>
</p>

#### 简介
项目集成字符验证码、运算验证码、滑动验证码 <br>
具有代码简洁、注释完备、配置灵活、易于上手的特点，适合所有开发者集成

#### 功能

- 字符验证码
- 运算验证码
- 滑动验证码

#### 快速访问

1. 快速访问:  <a href="http://localhost:8080/captcha/index">http://localhost:8080/captcha/index</a> <br/>
2. 字符验证码： <a href="http://localhost:8080/captcha/index?type=char">http://localhost:8080/captcha/index?type=char</a> <br/>
3. 运算验证码: <a href="http://localhost:8080/captcha/index?type=operation">http://localhost:8080/captcha/index?type=operation</a> <br/>
4. 滑动验证码: <a href="http://localhost:8080/captcha/index?type=slide">http://localhost:8080/captcha/index?type=slide</a> <br/>

#### 使用说明

字符验证码、运算验证码、滑动验证码有工具类，开箱即用；同时，字符验证码、运算验证码集成了google的kaptcha验证码 <br/>

- 字符验证码:  <br/>
默认使用kaptcha验证码。开箱即用，不需要任何配置就可以访问 <br/>
- 运算验证码: <br/>
默认使用kaptcha验证码。开箱即用，已经将运算器类配置好。如果需要更改和自定义运算方法请移步：<br/>
config.selfimpr.captcha  ->  config  ->  CaptchaConfig  ->  getKaptchaBeanMath()
- 滑动验证码: <br/>
需要将自己的验证码图片配置到application.yml文件路径中。相关的验证码图片和模板在以下位置：
项目中resources  ->  static ->  targets和templates目录<br/>

#### 许可证
[![license](https://img.shields.io/badge/License-MIT-orange?style=flat-square)](https://img.shields.io/badge/License-MIT-orange) <br/>

#### 预览图
<a href="https://gitee.com/gester/captcha/blob/develop/src/main/resources/static/img/1.png">预览图1</a>
<a href="https://gitee.com/gester/captcha/blob/develop/src/main/resources/static/img/2.png">预览图2</a>
<a href="https://gitee.com/gester/captcha/blob/develop/src/main/resources/static/img/3.png">预览图3</a>
<a href="https://gitee.com/gester/captcha/blob/develop/src/main/resources/static/img/4.png">预览图4</a>
<a href="https://gitee.com/gester/captcha/blob/develop/src/main/resources/static/img/5.png">预览图5</a> 



