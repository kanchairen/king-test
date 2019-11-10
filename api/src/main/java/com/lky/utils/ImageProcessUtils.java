package com.lky.utils;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.lky.commons.utils.StringUtils;
import com.lky.image.ImageDecorator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;

/**
 * 图片处理工具
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/19
 */
public class ImageProcessUtils {

    public static final String DEFAULT_FILE_EXT_NAME = "jpg";

    /*
     * 根据尺寸图片居中裁剪
     */
    public static void cutCenterImage(String src, String dest, String fileExtName, int w, int h) throws IOException {
        if (StringUtils.isEmpty(fileExtName)) {
            fileExtName = DEFAULT_FILE_EXT_NAME;
        }
        Iterator iterator = ImageIO.getImageReadersByFormatName(fileExtName);
        ImageReader reader = (ImageReader) iterator.next();
        InputStream in = new FileInputStream(src);
        ImageInputStream iis = ImageIO.createImageInputStream(in);
        reader.setInput(iis, true);
        ImageReadParam param = reader.getDefaultReadParam();
        int imageIndex = 0;
        Rectangle rect = new Rectangle((reader.getWidth(imageIndex) - w) / 2, (reader.getHeight(imageIndex) - h) / 2, w, h);
        param.setSourceRegion(rect);
        BufferedImage bi = reader.read(0, param);
        ImageIO.write(bi, fileExtName, new File(dest));
    }

    /*
     * 图片裁剪二分之一
     */
    public static void cutHalfImage(String src, String dest, String fileExtName) throws IOException {
        if (StringUtils.isEmpty(fileExtName)) {
            fileExtName = DEFAULT_FILE_EXT_NAME;
        }
        Iterator iterator = ImageIO.getImageReadersByFormatName(fileExtName);
        ImageReader reader = (ImageReader) iterator.next();
        InputStream in = new FileInputStream(src);
        ImageInputStream iis = ImageIO.createImageInputStream(in);
        reader.setInput(iis, true);
        ImageReadParam param = reader.getDefaultReadParam();
        int imageIndex = 0;
        int width = reader.getWidth(imageIndex) / 2;
        int height = reader.getHeight(imageIndex) / 2;
        Rectangle rect = new Rectangle(width / 2, height / 2, width, height);
        param.setSourceRegion(rect);
        BufferedImage bi = reader.read(0, param);
        ImageIO.write(bi, fileExtName, new File(dest));
    }

    /*
     * 图片裁剪通用接口
	 */
    public static void cutImage(String src, String dest, String fileExtName, int x, int y, int w, int h) throws IOException {
        if (StringUtils.isEmpty(fileExtName)) {
            fileExtName = DEFAULT_FILE_EXT_NAME;
        }
        Iterator iterator = ImageIO.getImageReadersByFormatName(fileExtName);
        ImageReader reader = (ImageReader) iterator.next();
        InputStream in = new FileInputStream(src);
        ImageInputStream iis = ImageIO.createImageInputStream(in);
        reader.setInput(iis, true);
        ImageReadParam param = reader.getDefaultReadParam();
        Rectangle rect = new Rectangle(x, y, w, h);
        param.setSourceRegion(rect);
        BufferedImage bi = reader.read(0, param);
        ImageIO.write(bi, fileExtName, new File(dest));

    }

    public static void cutImage(InputStream inputStream, String dest, String fileExtName, int x, int y, int w, int h) throws IOException {
        if (StringUtils.isEmpty(fileExtName)) {
            fileExtName = DEFAULT_FILE_EXT_NAME;
        }
        Iterator iterator = ImageIO.getImageReadersByFormatName(fileExtName);
        ImageInputStream iis = ImageIO.createImageInputStream(inputStream);
        ImageReader reader = (ImageReader) iterator.next();
        reader.setInput(iis, true);
        ImageReadParam param = reader.getDefaultReadParam();
        Rectangle rect = new Rectangle(x, y, w, h);
        param.setSourceRegion(rect);
        BufferedImage bi = reader.read(0, param);
        ImageIO.write(bi, fileExtName, new File(dest));
    }

    public static void cutImage(String file, String fileExtName, ImageDecorator decorator) throws IOException {
        cutImage(file, file, fileExtName, decorator.getCropX(), decorator.getCropY(), decorator.getCropWidth(), decorator.getCropHeight());
    }

    public static void cutImage(InputStream inputStream, String dest, String fileExtName, ImageDecorator decorator) throws IOException {
        cutImage(inputStream, dest, fileExtName, decorator.getCropX(), decorator.getCropY(), decorator.getCropWidth(), decorator.getCropHeight());
    }

    /*
     * 图片缩放
     */
    public static void zoomImage(String src, String dest, int w, int h) throws Exception {
        double wr, hr;
        File srcFile = new File(src);
        File destFile = new File(dest);
        BufferedImage bufImg = ImageIO.read(srcFile);
        Image imageTemp = bufImg.getScaledInstance(w, h, BufferedImage.SCALE_SMOOTH);
        wr = w * 1.0 / bufImg.getWidth();
        hr = h * 1.0 / bufImg.getHeight();
        AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, hr), null);
        imageTemp = ato.filter(bufImg, null);
        ImageIO.write((BufferedImage) imageTemp, dest.substring(dest.lastIndexOf(".") + 1), destFile);
    }

    /**
     * 图片旋转
     */
    public static BufferedImage rotate(Image src, int angel) {
        if (src == null) {
            return null;
        }
        int srcWidth = src.getWidth(null);
        int srcHeight = src.getHeight(null);
        // calculate the new image size
        Rectangle rectDes = calcRotatedSize(new Rectangle(new Dimension(
                srcWidth, srcHeight)), angel);

        BufferedImage res = null;
        res = new BufferedImage(rectDes.width, rectDes.height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = res.createGraphics();
        // transform
        g2.translate((rectDes.width - srcWidth) / 2,
                (rectDes.height - srcHeight) / 2);
        g2.rotate(Math.toRadians(angel), srcWidth / 2, srcHeight / 2);

        g2.drawImage(src, null, null);
        return res;
    }

    /**
     * 图片旋转
     */
    public static Rectangle calcRotatedSize(Rectangle src, int angel) {
        // if angel is greater than 90 degree, we need to do some conversion
        if (angel >= 90) {
            if (angel / 90 % 2 == 1) {
                int temp = src.height;
                src.height = src.width;
                src.width = temp;
            }
            angel = angel % 90;
        }

        double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;
        double len = 2 * Math.sin(Math.toRadians(angel) / 2) * r;
        double angelAlpha = (Math.PI - Math.toRadians(angel)) / 2;
        double angelDaltaWidth = Math.atan((double) src.height / src.width);
        double angelDaltaHeight = Math.atan((double) src.width / src.height);

        int lenDaltaWidth = (int) (len * Math.cos(Math.PI - angelAlpha
                - angelDaltaWidth));
        int lenDaltaHeight = (int) (len * Math.cos(Math.PI - angelAlpha
                - angelDaltaHeight));
        int desWidth = src.width + lenDaltaWidth * 2;
        int desHeight = src.height + lenDaltaHeight * 2;
        return new Rectangle(new Dimension(desWidth, desHeight));
    }

    /**
     * 获取旋转后的图片流
     */
    public static void rotateImg(InputStream inputStream, String dest, String fileExtName) throws ImageProcessingException, IOException, MetadataException {
        if (StringUtils.isEmpty(fileExtName)) {
            fileExtName = DEFAULT_FILE_EXT_NAME;
        }
        if (!fileExtName.equals(DEFAULT_FILE_EXT_NAME)) {
            return;
        }
        File destFile = new File(dest);
        inputStreamToFile(inputStream, destFile);
        Metadata metadata = ImageMetadataReader.readMetadata(destFile);
        Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        int orientation = 0;
        // Exif信息中有保存方向,把信息复制到缩略图
        if (directory != null && directory.containsTag(ExifDirectoryBase.TAG_ORIENTATION)) {
            // 原图片的方向信息
            orientation = directory.getInt(ExifDirectoryBase.TAG_ORIENTATION);
        }
        int angle = 0;
        if (6 == orientation) {
            //6旋转90
            angle = 90;
        } else if (3 == orientation) {
            //3旋转180
            angle = 180;
        } else if (8 == orientation) {
            //8旋转270
            angle = 270;
        }
        BufferedImage src = ImageIO.read(destFile);
        BufferedImage des = ImageProcessUtils.rotate(src, angle);
        ImageIO.write(des, fileExtName, destFile);
    }

    public static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, ImageProcessingException, MetadataException {
        String filePath = "/Users/luckyhua/Downloads/";
        File file = new File(filePath + "mmexport1512621687793.jpg");
        InputStream inputStream = new FileInputStream(file);
        ImageProcessUtils.rotateImg(inputStream, filePath + "0.jpg", "jpg");
//        cutImage(filePath + "00.png", filePath + "3.png", "png", 65, 40, 250, 160);

//        Metadata metadata = ImageMetadataReader.readMetadata(inputStream);
//        Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

    }

}
