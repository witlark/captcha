package com.selfimpr.captcha.utils;


import com.selfimpr.captcha.exception.ServiceException;
import com.selfimpr.captcha.exception.code.ServiceExceptionCode;
import com.selfimpr.captcha.model.vo.ImageVerificationVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;
import java.util.Random;

/**
 * @Description: 图片验证工具
 * -------------------
 * @Author: YangXingfu
 * @Date: 2019/07/24 18:40
 */

public class ImageVerificationUtil {

    private static final Logger log = LoggerFactory.getLogger(ImageVerificationUtil.class);

    /**
     * 默认图片宽度
     */
    private static final int DEFAULT_IMAGE_WIDTH = 280;

    /**
     * 默认图片高度
     */
    private static final int DEFAULT_IMAGE_HEIGHT = 171;

    /**
     * 获取request对象
     * @return 返回request对象
     */
    protected static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 获取response对象
     * @return 返回response对象
     */
    protected static HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }

    /**
     * 生成感兴趣区域坐标
     * @param verificationImage 源图
     * @param templateImage 模板图
     * @return 裁剪坐标
     */
    public static ImageVerificationVo generateCutoutCoordinates(BufferedImage verificationImage, BufferedImage templateImage) {

        int x, y;
        ImageVerificationVo imageVerificationVo = null;

        //  原图宽度
//        int VERIFICATION_IMAGE_WIDTH = verificationImage.getWidth();
        //  原图高度
//        int VERIFICATION_IMAGE_HEIGHT = verificationImage.getHeight();
        //  抠图模板宽度
        int templateImageWidth = templateImage.getWidth();
        //  抠图模板高度
        int templateImageHeight = templateImage.getHeight();

        Random random = new Random(System.currentTimeMillis());

        //  取范围内坐标数据，坐标抠图一定要落在原图中，否则会导致程序错误
        x = random.nextInt(DEFAULT_IMAGE_WIDTH - templateImageWidth) % (DEFAULT_IMAGE_WIDTH - templateImageWidth - templateImageWidth + 1) + templateImageWidth;
        y = random.nextInt(DEFAULT_IMAGE_HEIGHT - templateImageWidth) % (DEFAULT_IMAGE_HEIGHT - templateImageWidth - templateImageWidth + 1) + templateImageWidth;
        if (templateImageHeight - DEFAULT_IMAGE_HEIGHT >= 0) {
            y = random.nextInt(10);
        }
        imageVerificationVo = new ImageVerificationVo();
        imageVerificationVo.setX(x);
        imageVerificationVo.setY(y);

        return imageVerificationVo;
    }

    /**
     * 根据模板图裁剪图片，生成源图遮罩图和裁剪图
     * @param originImageFile 源图文件
     * @param originImageFileType 源图文件扩展名
     * @param templateImageFile 模板图文件
     * @param templateImageFileType 模板图文件扩展名
     * @param x 感兴趣区域X轴
     * @param y 感兴趣区域Y轴
     * @return
     * @throws ServiceException
     */
    public static ImageVerificationVo pictureTemplateCutout(File originImageFile, String originImageFileType, File templateImageFile, String templateImageFileType, int x, int y) throws ServiceException {
        ImageVerificationVo imageVerificationVo = null;
        ByteArrayOutputStream byteArrayOutputStream = null;


        try {
            //  读取模板图
            BufferedImage templateImage = ImageIO.read(templateImageFile);

            //  读取原图
            BufferedImage originImage = ImageIO.read(originImageFile);
            int templateImageWidth = templateImage.getWidth();
            int templateImageHeight = templateImage.getHeight();

            //  切块图   根据模板图尺寸创建一张透明图片
            BufferedImage cutoutImage = new BufferedImage(templateImageWidth, templateImageHeight, templateImage.getType());

            //  根据坐标获取感兴趣区域
            BufferedImage interestArea = getInterestArea(x, y, templateImageWidth, templateImageHeight, originImageFile, originImageFileType);

            //  根据模板图片切图
            cutoutImage = cutoutImageByTemplateImage(interestArea, templateImage, cutoutImage);

            //  图片绘图
            int bold = 5;
            Graphics2D graphics = cutoutImage.createGraphics();
            graphics.setBackground(Color.white);

            //  设置抗锯齿属性
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setStroke(new BasicStroke(bold, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
            graphics.drawImage(cutoutImage, 0, 0, null);
            graphics.dispose();

            //  原图生成遮罩
            BufferedImage shadeImage = generateShadeByTemplateImage(originImage, templateImage, x, y);


            imageVerificationVo = new ImageVerificationVo();
            byteArrayOutputStream = new ByteArrayOutputStream();
            //  图片转为二进制字符串
            ImageIO.write(originImage, originImageFileType, byteArrayOutputStream);
            byte[] originImageBytes = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.flush();
            byteArrayOutputStream.reset();
            //  图片加密成base64字符串
            String originImageString = Base64Utils.encodeToString(originImageBytes);
            imageVerificationVo.setOriginImage(originImageString);

            ImageIO.write(shadeImage, templateImageFileType, byteArrayOutputStream);
            //  图片转为二进制字符串
            byte[] shadeImageBytes = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.flush();
            byteArrayOutputStream.reset();
            //  图片加密成base64字符串
            String shadeImageString = Base64Utils.encodeToString(shadeImageBytes);
            imageVerificationVo.setShadeImage(shadeImageString);

            ImageIO.write(cutoutImage, templateImageFileType, byteArrayOutputStream);
            //  图片转为二进制字符串
            byte[] cutoutImageBytes = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.reset();
            //  图片加密成base64字符串
            String cutoutImageString = Base64Utils.encodeToString(cutoutImageBytes);
            imageVerificationVo.setCutoutImage(cutoutImageString);


        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(ServiceExceptionCode.IO_EXCEPTION);
        } finally {
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new ServiceException(ServiceExceptionCode.IO_EXCEPTION);
            }
        }
        return imageVerificationVo;
    }

    /**
     * 根据模板图生成遮罩图
     * @param originImage 源图
     * @param templateImage 模板图
     * @param x 感兴趣区域X轴
     * @param y 感兴趣区域Y轴
     * @return 遮罩图
     * @throws IOException 数据转换异常
     */
    private static BufferedImage generateShadeByTemplateImage(BufferedImage originImage, BufferedImage templateImage, int x, int y) throws IOException {
        //  根据原图，创建支持alpha通道的rgb图片
        BufferedImage shadeImage = new BufferedImage(originImage.getWidth(), originImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        //  原图片矩阵
        int[][] originImageMatrix = getMatrix(originImage);
        //  模板图片矩阵
        int[][] templateImageMatrix = getMatrix(templateImage);

        //  将原图的像素拷贝到遮罩图
        for (int i = 0; i < originImageMatrix.length; i++) {
            for (int j = 0; j < originImageMatrix[0].length; j++) {
                int rgb = originImage.getRGB(i, j);
                //  获取rgb色度
                int r = (0xff & rgb);
                int g = (0xff & (rgb >> 8));
                int b = (0xff & (rgb >> 16));
                //  无透明处理
                rgb = r + (g << 8) + (b << 16) + (255 << 24);
                shadeImage.setRGB(i, j, rgb);
            }
        }

        //  对遮罩图根据模板像素进行处理
        for (int i = 0; i < templateImageMatrix.length; i++) {
            for (int j = 0; j < templateImageMatrix[0].length; j++) {
                int rgb = templateImage.getRGB(i, j);

                //对源文件备份图像(x+i,y+j)坐标点进行透明处理
                if (rgb != 16777215 && rgb < 0) {
                    int originRGB = shadeImage.getRGB(x + i, y + j);
                    int r = (0xff & originRGB);
                    int g = (0xff & (originRGB >> 8));
                    int b = (0xff & (originRGB >> 16));


                    originRGB = r + (g << 8) + (b << 16) + (140 << 24);

                    //  对遮罩透明处理
                    shadeImage.setRGB(x + i, y + j, originRGB);
                    //  设置遮罩颜色
//                    shadeImage.setRGB(x + i, y + j, originRGB);

                }

            }
        }

        return shadeImage;
    }

    /**
     * 根据模板图抠图
     * @param interestArea  感兴趣区域图
     * @param templateImage  模板图
     * @param cutoutImage 裁剪图
     * @return 裁剪图
     */
    private static BufferedImage cutoutImageByTemplateImage(BufferedImage interestArea, BufferedImage templateImage, BufferedImage cutoutImage) {
        //  获取兴趣区域图片矩阵
        int[][] interestAreaMatrix = getMatrix(interestArea);
        //  获取模板图片矩阵
        int[][] templateImageMatrix = getMatrix(templateImage);

        //  将模板图非透明像素设置到剪切图中
        for (int i = 0; i < templateImageMatrix.length; i++) {
            for (int j = 0; j < templateImageMatrix[0].length; j++) {
                int rgb = templateImageMatrix[i][j];
                if (rgb != 16777215 && rgb < 0) {
                    cutoutImage.setRGB(i, j, interestArea.getRGB(i, j));
                }
            }
        }

        return cutoutImage;
    }

    /**
     * 图片生成图像矩阵
     * @param bufferedImage  图片源
     * @return 图片矩阵
     */
    private static int[][] getMatrix(BufferedImage bufferedImage) {
        int[][] matrix = new int[bufferedImage.getWidth()][bufferedImage.getHeight()];
        for (int i = 0; i < bufferedImage.getWidth(); i++) {
            for (int j = 0; j < bufferedImage.getHeight(); j++) {
                matrix[i][j] = bufferedImage.getRGB(i, j);
            }
        }
        return matrix;
    }

    /**
     * 获取感兴趣区域
     * @param x 感兴趣区域X轴
     * @param y 感兴趣区域Y轴
     * @param templateImageWidth  模板图宽度
     * @param templateImageHeight 模板图高度
     * @param originImage 源图
     * @param originImageType 源图扩展名
     * @return
     * @throws ServiceException
     */
    private static BufferedImage getInterestArea(int x, int y, int templateImageWidth, int templateImageHeight, File originImage, String originImageType) throws ServiceException {
        ImageInputStream imageInputStream = null;

        try {
            Iterator<ImageReader> imageReaderIterator = ImageIO.getImageReadersByFormatName(originImageType);
            ImageReader imageReader = imageReaderIterator.next();
            //  获取图片流
            imageInputStream = ImageIO.createImageInputStream(originImage);
            //  图片输入流顺序读写
            imageReader.setInput(imageInputStream, true);

            ImageReadParam imageReadParam = imageReader.getDefaultReadParam();

            //  根据坐标生成矩形
            Rectangle rectangle = new Rectangle(x, y, templateImageWidth, templateImageHeight);
            imageReadParam.setSourceRegion(rectangle);
            BufferedImage interestImage = imageReader.read(0, imageReadParam);
            return interestImage;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(ServiceExceptionCode.IO_EXCEPTION);
        } finally {
            try {
                imageInputStream.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new ServiceException(ServiceExceptionCode.IO_EXCEPTION);
            }
        }
    }

    /**
     * 切块图描边
     * @param imageVerificationVo 图片容器
     * @param borderImage 描边图
     * @param borderImageFileType 描边图类型
     * @return 图片容器
     * @throws ServiceException 图片描边异常
     */
    public static ImageVerificationVo cutoutImageEdge(ImageVerificationVo imageVerificationVo, BufferedImage borderImage, String borderImageFileType) throws ServiceException{

        ByteArrayInputStream byteArrayInputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;

        try {
            String cutoutImageString = imageVerificationVo.getCutoutImage();
            //  图片解密成二进制字符创
            byte[] bytes = Base64Utils.decodeFromString(cutoutImageString);
            byteArrayInputStream = new ByteArrayInputStream(bytes);
            //  读取图片
            BufferedImage cutoutImage = ImageIO.read(byteArrayInputStream);
            //  获取模板边框矩阵， 并进行颜色处理
            int[][] borderImageMatrix = getMatrix(borderImage);
            for (int i = 0; i < borderImageMatrix.length; i++) {
                for (int j = 0; j < borderImageMatrix[0].length; j++) {
                    int rgb = borderImage.getRGB(i, j);
                    if (rgb < 0) {
                        cutoutImage.setRGB(i, j , -7237488);
                    }
                }
            }
            byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(cutoutImage, borderImageFileType, byteArrayOutputStream);
            //  新模板图描边处理后转成二进制字符串
            byte[] cutoutImageBytes = byteArrayOutputStream.toByteArray();
            //  二进制字符串加密成base64字符串
            String cutoutImageStr = Base64Utils.encodeToString(cutoutImageBytes);
            imageVerificationVo.setCutoutImage(cutoutImageStr);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(ServiceExceptionCode.IO_EXCEPTION);
        } finally {
            try {
                byteArrayInputStream.close();
                byteArrayOutputStream.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new ServiceException(ServiceExceptionCode.IO_EXCEPTION);
            }
        }
        return imageVerificationVo;
    }
}