package com.lky.dto;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 数据库检索转化传递
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/11/8
 */
public class ShopCountDto implements RowMapper<ShopCountRowDto> {
    @Override
    public ShopCountRowDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        ShopCountRowDto shopCountRow = new ShopCountRowDto();
        shopCountRow.setLineX(rs.getString("x"));
        shopCountRow.setLineY(rs.getObject("y"));
        return shopCountRow;
    }
}
