package com.selfimpr.captcha.service.impl;


import com.selfimpr.captcha.controller.CaptchaController;
import com.selfimpr.captcha.exception.ServiceException;
import com.selfimpr.captcha.exception.code.ServiceExceptionCode;
import com.selfimpr.captcha.service.CaptchaService;
import com.selfimpr.captcha.utils.ImageVerificationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Random;

/**
 * @Description: -------------------
 * @Author: YangXingfu
 * @Date: 2019/07/24 17:16
 */

@Service
public class CaptchaServiceImpl implements CaptchaService {

    /**
     * 日志
     */
    private static final Logger log = LoggerFactory.getLogger(CaptchaController.class);

    /**
     * 源图路径前缀
     */
    @Value("${captcha.slide-verification-code.path.origin-image}")
    private String verificationImagePathPrefix;

    /**
     * 模板图路径前缀
     * @return
     */
    @Value("${captcha.slide-verification-code.path.template-image}")
    private String templateImagePathPrefix;

    protected static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    protected static HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }




    @Override
    public Map<String, String> getVerificationImage() throws ServiceException {


        Map<String, String> pictureMap = null;
        try {
            //  原图路径
            String verifyImagePath = URLDecoder.decode(this.getClass().getResource("/").getPath() + "static/targets", "UTF-8");
            //  模板图路径
            String templateImagePath = URLDecoder.decode(this.getClass().getResource("/").getPath() + "static/templates", "UTF-8");
            //  描边图片路径
            String borderImagePath = URLDecoder.decode(this.getClass().getResource("/").getPath() + "static/templates", "UTF-8");
            String path = URLDecoder.decode(ResourceUtils.getURL("classpath:").getPath(), "UTF-8");
            System.out.println(path + "=======================================================");
            File verifyImageImport = new File(verifyImagePath);
            File templateImageImport = new File(templateImagePath);
            //  获取原图所有图片
            File[] verifyImages = verifyImageImport.listFiles();
            //  获取模板图所有图片
            File[] templateImages = templateImageImport.listFiles();

            Random random = new Random(System.currentTimeMillis());
            //  随机取得原图文件夹中一张图片
            File originImageFile = verifyImages[random.nextInt(verifyImages.length)];
            //  随机取得模板图文件夹中一张图片
            File templateImageFile = templateImages[random.nextInt(templateImages.length)];

            //  读取描边图片
            File borderImageFile = new File(borderImagePath + "/border.png");
            //  读取描边图片类型
            String borderImageFileType = borderImageFile.getName().substring(borderImageFile.getName().lastIndexOf(".") + 1);

            //  获得原图文件类型
            String originImageFileType = originImageFile.getName().substring(originImageFile.getName().lastIndexOf(".") + 1);
            //  获取模板图文件类型
            String templateImageFileType = templateImageFile.getName().substring(templateImageFile.getName().lastIndexOf(".") + 1);

            //  读取原图
            BufferedImage verificationImage = ImageIO.read(originImageFile);
            //  读取模板图
            BufferedImage readTemplateImage = ImageIO.read(templateImageFile);

            //  读取描边图片
            BufferedImage borderImage = ImageIO.read(borderImageFile);


            //  获取原图感兴趣区域坐标
            Map<String, Integer> XYMap= ImageVerificationUtil.generateCutoutCoordinates(verificationImage, readTemplateImage);
            getRequest().getSession().setAttribute("ImageXYMap", XYMap);

            int X = XYMap.get("X");
            int Y = XYMap.get("Y");
            //  根据原图生成遮罩图和切块图
            pictureMap = ImageVerificationUtil.pictureTemplateCutout(originImageFile, originImageFileType, templateImageFile, templateImageFileType, X, Y);

            //   剪切图描边
            pictureMap = ImageVerificationUtil.cutoutImageEdge(pictureMap, borderImage, borderImageFileType);

            pictureMap.put("Y", String.valueOf(Y));



            //  =============================================
            //  输出图片
//            HttpServletResponse response = getResponse();
//            response.setContentType("image/jpeg");
//            ServletOutputStream outputStream = response.getOutputStream();
////            outputStream.write(oriCopyImages);
//            BufferedImage bufferedImage = ImageIO.read(originImageFile);
//            ImageIO.write(bufferedImage, originImageType, outputStream);
//            outputStream.flush();
            //  =================================================

        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(ServiceExceptionCode.URL_DECODER_ERROR);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(ServiceExceptionCode.IO_EXCEPTON);
        }

        return pictureMap;
    }

    @Override
    public boolean checkVerificationResult(String X, String Y) throws ServiceException {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            Map<String, Integer> XYMap = (Map<String, Integer>) request.getSession().getAttribute("ImageXYMap");
            if (XYMap != null) {
                if ((Math.abs(Integer.parseInt(X) - XYMap.get("X")) <= 5) && Y.equals(XYMap.get("Y").toString())) {
                    System.out.println("验证成功");
                    return true;
                } else {
                    System.out.println("验证失败");
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(ServiceExceptionCode.IO_EXCEPTON);
        }
    }
}