package com.lky.modules.biz.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.dto.ImageDto;
import com.lky.entity.User;
import com.lky.global.annotation.LoginUser;
import com.lky.image.ImageConfig;
import com.lky.service.ImageService;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import java.util.List;

import static com.lky.commons.code.PublicResCode.NOT_AUTHORIZED;
import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;

/**
 * 图片操作
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/11
 */
@RestController
@RequestMapping("biz/image")
@Api(value = "biz/image", description = "图片操作")
public class BImageController extends BaseController {

    @Inject
    private ImageService imageService;

    @ApiOperation(value = "单张图片上传", response = ImageDto.class, notes = "image")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "decorator", value = "裁剪尺寸（json格式）", paramType = "query", dataType = "String")
    })
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseInfo addFile(@ApiIgnore @LoginUser User user,
                                @ApiParam(name = "file", value = "上传的图片") @RequestParam("file") MultipartFile file,
                                @ApiParam(name = "type", value = "图片类型") @RequestParam String type,
                                @RequestParam(required = false) String decorator) {
        ImageDto imageDto = imageService.singleLoad(user.getId(), file, type, decorator);
        ResponseInfo responseInfo = ResponseInfo.buildSuccessResponseInfo();
        responseInfo.putData("image", imageDto);
        return responseInfo;
    }

    @ApiOperation(value = "多张张图片上传", response = ImageDto.class, notes = "imageList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "上传多张图片", required = true, paramType = "query", dataType = "MultipartFile[]"),
            @ApiImplicitParam(name = "type", value = "上传的图片类型", required = true,
                    paramType = "query", dataType = "String")
    })
    @RequestMapping(value = "files", method = RequestMethod.POST)
    public ResponseInfo addFiles(@ApiIgnore @LoginUser User user,
                                 @ApiParam(name = "file", value = "上传的图片") @RequestParam("file") MultipartFile[] files,
                                 @ApiParam(name = "type", value = "图片类型") @RequestParam String type) {
        List<ImageDto> imageList = imageService.batchUploadImage(user.getId(), files, type);
        ResponseInfo responseInfo = ResponseInfo.buildSuccessResponseInfo();
        responseInfo.putData("imageList", imageList);
        return responseInfo;
    }

    /**
     * 删除阿里云上的图片
     */
    @ApiOperation(value = "多张张图片上传", response = ImageDto.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "url", value = "阿里云图片删除", required = true,
                    paramType = "query", dataType = "String"),
    })
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public Object delete(@ApiIgnore @LoginUser User user, @RequestParam String url) {
        AssertUtils.notNull(PARAMS_EXCEPTION, url);
        AssertUtils.isTrue(NOT_AUTHORIZED, url.contains(ImageConfig.AO_ROOT_PREFIX + "/user/" + user.getId() + "/"));
        if (imageService.removeUrl(url)) {
            return ResponseInfo.buildSuccessResponseInfo();
        }
        return ResponseInfo.buildErrorResponseInfo();
    }
}
