package com.lky.modules.act.controller;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.util.Config;
import com.lky.commons.base.BaseController;
import com.lky.global.constant.Constant;
import com.lky.utils.ShiroUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Properties;

/**
 * 验证码
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/20
 */
@Controller
@RequestMapping(value = "act")
@Api(value = "act", description = "图形验证码")
public class AKaptchaController extends BaseController {

    public static final String KAPTCHA_IMAGE_FORMAT = "jpeg";

    private Properties props = new Properties();
    private Producer kaptchaProducer = null;

    /**
     * 生成验证码初始化配置
     */
    public AKaptchaController() {
        ImageIO.setUseCache(false);

        //设置宽和高。
        this.props.put(Constants.KAPTCHA_IMAGE_WIDTH, "200");
        this.props.put(Constants.KAPTCHA_IMAGE_HEIGHT, "60");
        //kaptcha.border：是否显示边框。
        this.props.put(Constants.KAPTCHA_BORDER, "no");
        //kaptcha.textproducer.font.color：字体颜色
        this.props.put(Constants.KAPTCHA_TEXTPRODUCER_FONT_COLOR, "blue");
        //kaptcha.textproducer.char.space：字符间距
        this.props.put(Constants.KAPTCHA_TEXTPRODUCER_CHAR_SPACE, "5");
        //设置字体。
        this.props.put(Constants.KAPTCHA_TEXTPRODUCER_FONT_SIZE, "40");
        //设置验证码长度
        this.props.put(Constants.KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, "4");

        Config config = new Config(this.props);
        this.kaptchaProducer = config.getProducerImpl();
    }

    @ApiOperation(value = "获取登录验证码")
    @GetMapping(value = "/captcha.jpg")
    @ResponseBody
    public void kaptcha(HttpServletResponse response) throws IOException {
        // flush it in the response
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/" + KAPTCHA_IMAGE_FORMAT);

        String capText = this.kaptchaProducer.createText();
        BufferedImage bi = this.kaptchaProducer.createImage(capText);

        //保存到shiro session
        ShiroUtils.setSessionAttribute(Constant.AKAPTCHA_SESSION_KEY, capText);

        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(bi, KAPTCHA_IMAGE_FORMAT, out);
        IOUtils.closeQuietly(out);
    }
}
