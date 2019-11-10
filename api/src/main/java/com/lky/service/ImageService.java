package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.utils.*;
import com.lky.dao.ImageDao;
import com.lky.dto.ImageDto;
import com.lky.entity.Image;
import com.lky.enums.code.ShopResCode;
import com.lky.enums.dict.ImageDict;
import com.lky.image.ImageConfig;
import com.lky.image.ImageDecorator;
import com.lky.image.ImageUtils;
import com.lky.mapper.ImageMapper;
import com.lky.utils.ImageProcessUtils;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOException;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.commons.code.PublicResCode.SERVER_EXCEPTION;
import static com.lky.enums.code.ShopResCode.IMAGE_TYPE_NOT_ALLOW;
import static com.lky.enums.dict.ImageDict.TYPE_USER_AVATAR;

/**
 * 图片操作
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/11
 */
@Service
public class ImageService extends BaseService<Image, Integer> {

    private static final Logger log = LoggerFactory.getLogger(ImageService.class);
    /**
     * 上传文件的本地临时保存地址
     */
    public static final String TMP_UPLOAD_DIR = "tmp_upload/";

    /**
     * 富文本中批量上传的单张图片不能大于2M
     */
    public static final int IMAGE_SIZE = 2 * 1024 * 1024;

    @Inject
    private ImageDao imageDao;

    @Inject
    private ImageMapper imageMapper;

    @Override
    public BaseDao<Image, Integer> getBaseDao() {
        return this.imageDao;
    }

    /**
     * 上传处理图片
     *
     * @param inputStream      图片流
     * @param decorator        裁剪参数
     * @param type             图片枚举
     * @param originalFileName 全名
     * @param userId           用户id
     * @return 图片
     */
    public Image uploadImage(InputStream inputStream, ImageDecorator decorator, String type,
                             String originalFileName, Integer userId) {

        String path = ImageDict.getValue(type).replace("{id}", userId + "");
        //调用上传图片方法
        Map<String, String> uploadResult = null;
        try {
            if (decorator != null) {
                verifyDecorator(decorator); //style参数校验
                File fileDir = new File(TMP_UPLOAD_DIR);
                if (!FileUtils.isFolder(fileDir)) {
                    FileUtils.createFolder(fileDir);
                }
                String fileExtName = ImageUtils.getExtFileName(originalFileName);
                String fileName = TMP_UPLOAD_DIR + StringUtils.getUUID(16)
                        + "." + fileExtName;
                ImageProcessUtils.rotateImg(inputStream, fileName, fileExtName);
                if (fileExtName.equals(ImageProcessUtils.DEFAULT_FILE_EXT_NAME)) {
                    ImageProcessUtils.cutImage(fileName, fileExtName, decorator);
                } else {
                    ImageProcessUtils.cutImage(inputStream, fileName, fileExtName, decorator);
                }
                File localFile = new File(fileName);
                if (TYPE_USER_AVATAR.compare(type)) {
                    Thumbnails.of(fileName).size(108, 108).toFile(localFile);
                }
                uploadResult = ImageUtils.upload(localFile, path);
                FileUtils.delete(localFile);
            } else {
                uploadResult = ImageUtils.upload(inputStream, path, originalFileName);
            }
        } catch (IIOException iioE) {
            ExceptionUtils.throwResponseException(IMAGE_TYPE_NOT_ALLOW);
        } catch (Exception e) {
            log.error("upload image exception : ", e);
            ExceptionUtils.throwResponseException(SERVER_EXCEPTION);
        }

        AssertUtils.notNull(SERVER_EXCEPTION, uploadResult);

        //返回图片
        Image image = new Image();
        image.setUrl(ImageConfig.AO_URL + "/" + uploadResult.get("key"));
        image.setType(type);
        image.setDeleted(Boolean.FALSE);
        image.setCreateTime(new Date());
        super.save(image);
        return image;
    }

    /**
     * 移除图片
     *
     * @param urls 图片链接
     * @return 是否移除
     */
    public boolean removeUrl(String... urls) {
        return ImageUtils.removeUrls(urls);
    }

    /**
     * 获取裁剪参数
     *
     * @param decorator 裁剪参数
     * @return 裁剪对象
     */
    public ImageDecorator getImageDecorator(String decorator) {
        ImageDecorator imageDecorator = null;
        if (!StringUtils.isEmpty(decorator)) {
            log.debug("decorator:" + decorator);
            try {
                imageDecorator = JsonUtils.jsonToObject(decorator, ImageDecorator.class);
            } catch (Exception e) {
                log.error("image decorator exception : ", e);
                ExceptionUtils.throwResponseException(PARAMS_EXCEPTION);
            }
        }
        return imageDecorator;
    }

    /**
     * 校验截取缩放参数
     *
     * @param imageDecorator 处理参数
     */
    private void verifyDecorator(ImageDecorator imageDecorator) {
        for (Field field : ImageDecorator.class.getDeclaredFields()) {
            if (field.getType().isPrimitive()) {
                field.setAccessible(true);
                try {
                    int value = (int) field.get(imageDecorator);
                    AssertUtils.isTrue(PARAMS_EXCEPTION, value >= 0);
                } catch (IllegalAccessException e) {
                    ExceptionUtils.throwResponseException(SERVER_EXCEPTION);
                }
            }
        }
    }


    /**
     * 图片批量上传
     *
     * @param userId 用户Id
     * @param files  上传的图片
     * @param type   图片类型
     * @return 图片dtoList
     */
    public List<ImageDto> batchUploadImage(Integer userId, MultipartFile[] files, String type) {
        //校验参数
        AssertUtils.isTrue(PARAMS_EXCEPTION, !CollectionUtils.isEmpty(files));
        Arrays.stream(files).forEach(file -> {
            AssertUtils.isTrue(PARAMS_EXCEPTION, !file.isEmpty());
            //效验单张图片的大小不能超过2M
            AssertUtils.isTrue(ShopResCode.IMAGE_LIMIT_SIZE, file.getSize() <= IMAGE_SIZE);
        });
        List<ImageDto> imageList = new ArrayList<>();
        Arrays.stream(files).forEach(multipartFile -> {
            try {
                Image image = this.uploadImage(multipartFile.getInputStream(), null,
                        type, multipartFile.getOriginalFilename(), userId);
                imageList.add(imageMapper.toDto(image));
            } catch (IOException e) {
                log.error("upload image exception : ", e);
                ExceptionUtils.throwResponseException(SERVER_EXCEPTION);
            }
        });
        return imageList;
    }

    /**
     * 单张图片上传
     *
     * @param userId    用户id
     * @param file      图片文件对象
     * @param type      图片类型
     * @param decorator 裁剪信息
     * @return 图片Dto
     */
    public ImageDto singleLoad(int userId, MultipartFile file, String type, String decorator) {
        //校验参数
        AssertUtils.isTrue(PARAMS_EXCEPTION, !file.isEmpty());
        ImageDecorator imageDecorator = this.getImageDecorator(decorator);
        Image image = null;
        try {
            image = this.uploadImage(file.getInputStream(), imageDecorator,
                    type, file.getOriginalFilename(), userId);
        } catch (IOException e) {
            log.error("upload image exception : ", e);
            ExceptionUtils.throwResponseException(SERVER_EXCEPTION);
        }
        return imageMapper.toDto(image);
    }
}
