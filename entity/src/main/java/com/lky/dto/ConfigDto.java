package com.lky.dto;

import com.lky.entity.BaseConfig;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 系统配置
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/20
 */
@ApiModel(value = "ConfigDto", description = "系统配置")
public class ConfigDto {

    @ApiModelProperty(notes = "小米和现金比例")
    private double rpointRate;

    @ApiModelProperty(notes = "G米和现金比例")
    private double wpointRate;

    private HighConfigDto highConfig;

    private BaseConfig baseConfig;

    public double getRpointRate() {
        return rpointRate;
    }

    public void setRpointRate(double rpointRate) {
        this.rpointRate = rpointRate;
    }

    public double getWpointRate() {
        return wpointRate;
    }

    public void setWpointRate(double wpointRate) {
        this.wpointRate = wpointRate;
    }

    public HighConfigDto getHighConfig() {
        return highConfig;
    }

    public void setHighConfig(HighConfigDto highConfig) {
        this.highConfig = highConfig;
    }

    public BaseConfig getBaseConfig() {
        return baseConfig;
    }

    public void setBaseConfig(BaseConfig baseConfig) {
        this.baseConfig = baseConfig;
    }
}
