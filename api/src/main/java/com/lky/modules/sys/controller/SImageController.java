package com.lky.modules.sys.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.dto.ImageDto;
import com.lky.image.ImageConfig;
import com.lky.service.ImageService;
import com.lky.utils.ShiroUtils;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
@RestController("sysImageController")
@RequestMapping("sys/image")
@Api(value = "sys/image", description = "图片操作")
public class SImageController extends BaseController {

    @Inject
    private ImageService imageService;

    @ApiOperation(value = "单张图片上传", response = ImageDto.class, notes = "image")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "decorator", value = "裁剪尺寸（json格式）", paramType = "query", dataType = "String")
    })
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseInfo addFile(@ApiParam(name = "file", value = "上传的图片") @RequestParam("file") MultipartFile file,
                                @ApiParam(name = "type", value = "图片类型") @RequestParam String type,
                                @RequestParam(required = false) String decorator) {
        ImageDto imageDto = imageService.singleLoad(ShiroUtils.getSUserId(), file, type, decorator);
        ResponseInfo responseInfo = ResponseInfo.buildSuccessResponseInfo();
        responseInfo.putData("image", imageDto);
        return responseInfo;
    }

    @ApiOperation(value = "多张图片上传", response = ImageDto.class, notes = "imageList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "files", value = "上传多张图片", required = true,
                    paramType = "query", dataType = "MultipartFile[]"),
            @ApiImplicitParam(name = "type", value = "上传的图片类型",
                    allowableValues = "dish, restaurant, other", required = true,
                    paramType = "query", dataType = "String")
    })
    @RequestMapping(value = "files", method = RequestMethod.POST)
    public ResponseInfo addFiles(@RequestParam("file") MultipartFile[] files,
                                 @ApiParam(name = "type", value = "图片类型") @RequestParam String type) {
        List<ImageDto> imageList = imageService.batchUploadImage(ShiroUtils.getSUserId(), files, type);
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
    public Object delete(@RequestParam String url) {
        AssertUtils.notNull(PARAMS_EXCEPTION, url);
        AssertUtils.isTrue(NOT_AUTHORIZED, url.contains(ImageConfig.AO_ROOT_PREFIX + "/user/" + ShiroUtils.getSUserId() + "/"));
        if (imageService.removeUrl(url)) {
            return ResponseInfo.buildSuccessResponseInfo();
        }
        return ResponseInfo.buildErrorResponseInfo();
    }
}
