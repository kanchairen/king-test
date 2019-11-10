package com.lky.mapper;

import com.lky.dto.UserAssetDto;
import com.lky.entity.UserAsset;
import org.mapstruct.Mapper;

/**
 * 用户资产dto传输
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/11/2
 */
@Mapper(componentModel = "jsr330")
public abstract class AssetMapper {

    public abstract UserAssetDto toDto(UserAsset userAsset);
}
