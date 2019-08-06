package com.selfimpr.captcha.utils;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * @Description: -------------------
 * @Author: YangXingfu
 * @Date: 2019/07/26 15:22
 */

public class Test {

    public static void main(String[] args) throws Exception {
        File file = new File("D:\\DevelopmentTools\\IntelliJ IDEA 2017.3.5\\ProjectFiles\\captcha\\src\\main\\resources\\static\\templates\\template2.png");

        BufferedImage bufferedImage = ImageIO.read(file);
        BufferedImage newImage = DealOriPictureByTemplate(bufferedImage);
        OutputStream outputStream = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\test.png");
        ImageIO.write(newImage, "png", outputStream);
        outputStream.flush();

    }


    private static BufferedImage  DealOriPictureByTemplate(BufferedImage oriImage) throws Exception {

        // 源文件备份图像矩阵 支持alpha通道的rgb图像
        BufferedImage ori_copy_image = new BufferedImage(oriImage.getWidth(), oriImage.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);


        // 源文件图像矩阵
        int[][] oriImageData = getMatrix(oriImage);

        //copy 源图做不透明处理
        for (int i = 0; i < oriImageData.length; i++) {
            for (int j = 0; j < oriImageData[0].length; j++) {
                int rgb = oriImage.getRGB(i, j);
                int r = (0xff & rgb);
                int g = (0xff & (rgb >> 8));
                int b = (0xff & (rgb >> 16));
                //无透明处理
                if (rgb < 0) {
                    rgb = r + (g << 8) + (b << 16) + (255 << 24);
                    ori_copy_image.setRGB(i, j, rgb);
                }
            }
        }

        return ori_copy_image;
    }

    private static int[][] getMatrix(BufferedImage bufferedImage) {
        int[][] matrix = new int[bufferedImage.getWidth()][bufferedImage.getHeight()];
        for (int i = 0; i < bufferedImage.getWidth(); i++) {
            for (int j = 0; j < bufferedImage.getHeight(); j++) {
                matrix[i][j] = bufferedImage.getRGB(i, j);
            }
        }
        return matrix;
    }

}