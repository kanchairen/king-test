package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.dao.OrdersItemDao;
import com.lky.dto.ShopCountDto;
import com.lky.dto.ShopCountRowDto;
import com.lky.entity.OrdersItem;
import com.lky.entity.Shop;
import com.lky.entity.User;
import com.lky.scheduling.ShopScheduling;
import net.sf.dynamicreports.jasper.builder.export.JasperXlsxExporterBuilder;
import net.sf.dynamicreports.jasper.constant.JasperProperty;
import net.sf.dynamicreports.report.builder.column.ColumnBuilder;
import net.sf.dynamicreports.report.exception.DRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.enums.code.MerchantResCode.NO_MERCHANT;
import static com.lky.enums.dict.ShopStatisticsDict.*;
import static net.sf.dynamicreports.report.builder.DynamicReports.*;

/**
 * 子订单
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/10/30
 */
@Service
public class OrdersItemService extends BaseService<OrdersItem, Integer> {

    private static final Logger log = LoggerFactory.getLogger(ShopScheduling.class);

    @Inject
    private OrdersItemDao ordersItemDao;

    @Inject
    private JdbcTemplate jdbcTemplate;

    @Inject
    private ShopService shopService;

    @Override
    public BaseDao<OrdersItem, Integer> getBaseDao() {
        return this.ordersItemDao;
    }

    public List<OrdersItem> findByOrdersId(String ordersId) {
        return ordersItemDao.findByOrdersId(ordersId);
    }

    public List<ShopCountRowDto> countSalesByShopId(User user, Date beginTime, Date endTime,
                                                    String countType, String groupType, Boolean desc) {
        Shop shop = shopService.findByUser(user);
        AssertUtils.notNull(NO_MERCHANT, shop);
        AssertUtils.isContain(PARAMS_EXCEPTION, countType, COUNT_SALES_NUMBER, COUNT_SALES_AMOUNT);
        AssertUtils.isContain(PARAMS_EXCEPTION, groupType, GROUP_BY_MONTH, GROUP_BY_DAY, GROUP_BY_CATEGORY);
        Integer shopId = shop.getId();
        String sql;
        String countSql;
        String groupSql;
        DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //HH表示24小时制；
        //统计中加入线下订单部分
        if (COUNT_SALES_AMOUNT.getKey().equals(countType) && !GROUP_BY_CATEGORY.getKey().equals(groupType)) {
            String groupSqlOff;
            String groupTotal;
            if (GROUP_BY_DAY.getKey().equals(groupType)) {
                groupSql = "DATE_FORMAT(o.create_time, '%Y-%m-%d') x";
                groupSqlOff = "DATE_FORMAT(f.create_time, '%Y-%m-%d') x";
                groupTotal = "DATE_FORMAT(total.x, '%Y-%m-%d')";

            } else {
                groupSql = "DATE_FORMAT(o.create_time,'%Y-%m') x";
                groupSqlOff = "DATE_FORMAT(f.create_time, '%Y-%m') x";
                groupTotal = "DATE_FORMAT(total.x, '%Y-%m')";
            }
            sql = "SELECT x, SUM(y) y FROM " +
                    //线上订单统计
                    "(SELECT " + groupSql + "," + " SUM(o.amount) y FROM m_orders o " +
                    "WHERE o.amount > 0  AND o.state != 'close' AND o.state != 'wait' \n" +
                    "AND o.shop_id = " + shopId +
                    " AND (o.create_time BETWEEN '" +
                    dFormat.format(beginTime) + "' AND '" + dFormat.format(endTime)
                    + "') GROUP BY " + groupSql.replace("x", "") +
                    //删除线上订单中的部分退款的子订单金额
                    " UNION ALL\n" +
                    "SELECT " + groupSql + ", -SUM(oi.price * oi.number) y  \n" +
                    "FROM m_orders_item oi \n" +
                    "LEFT JOIN m_orders_return ort ON oi.id = ort.orders_item_id \n" +
                    "INNER JOIN m_orders o ON oi.orders_id = o.id \n" +
                    "WHERE oi.price > 0 AND oi.shop_id = " + shopId +
                    " AND o.state != 'close' AND o.state != 'wait' " +
                    "AND ort.state = 'agree' AND ort.return_type != 'exchange' \n" +
                    "AND (o.create_time BETWEEN '" +
                    dFormat.format(beginTime) + "' AND '" + dFormat.format(endTime)
                    + "') GROUP BY " + groupSql.replace("x", "") +
                    //线下订单金额统计
                    " UNION ALL \n" +
                    "SELECT " + groupSqlOff + "," + " SUM(f.amount) y FROM m_offline_orders f " +
                    "WHERE f.amount > 0 AND f.state = 'paid' \n" +
                    "AND f.shop_id = " + shopId +
                    " AND (f.create_time BETWEEN '" +
                    dFormat.format(beginTime) + "' AND '" + dFormat.format(endTime)
                    + "') GROUP BY " + groupSqlOff.replace("x", "") + ")" + "AS total " +
                    "GROUP BY " + groupTotal;

        } else {
            if (COUNT_SALES_NUMBER.getKey().equals(countType)) {
                countSql = "SUM(oi.number) y";
            } else {
                countSql = "SUM(oi.number * oi.price) y";
            }
            if (GROUP_BY_DAY.getKey().equals(groupType)) {
                groupSql = "DATE_FORMAT(o.create_time, '%Y-%m-%d') x";
                //                groupSql = "date(pr.create_time) x";
            } else if (GROUP_BY_MONTH.getKey().equals(groupType)) {
                groupSql = "DATE_FORMAT(o.create_time,'%Y-%m') x";
            } else {
                groupSql = "c.name x";
            }

            sql = "select " + countSql + "," + groupSql + " \n FROM  `m_orders_item` oi INNER JOIN m_orders o on oi.orders_id = o.id " +
                    "LEFT JOIN m_orders_return ort ON oi.id = ort.orders_item_id \n";

            if (GROUP_BY_CATEGORY.getKey().equals(groupType)) {
                sql += "LEFT JOIN m_product p ON oi.product_id = p.id LEFT JOIN m_product_group pg ON p.product_group_id = pg.id LEFT JOIN m_category c ON pg.category_id = c.id \n";
            }

            sql += " WHERE (o.create_time between '" +
                    dFormat.format(beginTime) +
                    "' AND '" + dFormat.format(endTime) + "') AND o.state != 'wait' \n" +
                    "AND o.state != 'close' " +
                    "AND o.shop_id = " + shopId +
                    " AND (ort.state IS NULL OR (ort.state != 'agree' OR ort.return_type != 'exchange')) \n" +
                    " group by " + groupSql.replace("x", "");
        }
        if (desc != null) {
            if (desc) {
                sql += "order by y DESC";
            } else {
                sql += "order by y";
            }
        }
        return jdbcTemplate.query(sql, new ShopCountDto());
    }


    public String buildSalesReport(User user, Date beginTime, Date endTime, String countType, String groupType, Boolean desc) {

        File file = new File(REPORTS_DIR.getKey());
        if (!file.exists() && !file.mkdirs()) {
            log.error("mkdir 失败");
        }
        String reportFileName = REPORTS_DIR.getKey() + StringUtils.getUUID() + ".xlsx";
        List<ShopCountRowDto> countRowDtoList = this.countSalesByShopId(user, beginTime, endTime, countType, groupType, desc);
        List<Map<String, Object>> copyMap = new ArrayList<>();
        if (!CollectionUtils.isEmpty(countRowDtoList)) {
            for (ShopCountRowDto shopCountRowDto : countRowDtoList) {
                Map<String, Object> map = new HashMap<>(2);
                map.put("x", shopCountRowDto.getLineX());
                map.put("y", shopCountRowDto.getLineY());
                copyMap.add(map);
            }
        }

        String countTypeString = COUNT_SALES_NUMBER.getKey().equals(countType) ? "销售量（单位：个）" : "销售额（单位：元）";

        String groupTypeString = "";
        if (GROUP_BY_CATEGORY.getKey().equals(groupType)) {
            groupTypeString = "分类";
            createExcel(reportFileName, copyMap,
                    col.column(groupTypeString, "x", type.stringType()),
                    col.column(countTypeString, "y", type.bigDecimalType()));
        } else {
            if (GROUP_BY_MONTH.getKey().equals(groupType)) {
                groupTypeString = "月份";
            } else if (GROUP_BY_DAY.getKey().equals(groupType)) {
                groupTypeString = "日期";
            }
            //金额从分变为元，除以100
            if (COUNT_SALES_AMOUNT.getKey().equals(countType)) {
                for (int i = 0; i < copyMap.size(); i++) {
                    Map<String, Object> map = copyMap.get(i);
                    BigDecimal salesCount = (BigDecimal) map.get("y");
                    BigDecimal v2 = new BigDecimal(100);
                    //结果 两位小数、四舍五入 更多详细精度计算方式请参考API BigDecimal-字段摘要
                    map.put("y", salesCount.divide(v2).setScale(2, BigDecimal.ROUND_HALF_UP));
                }
            }
            createExcel(reportFileName, copyMap,
                    col.column(groupTypeString, "x", type.stringType()),
                    col.column(countTypeString, "y", type.bigDecimalType()));
        }

        return reportFileName;

    }

    protected void createExcel(String fileName, Collection collection, ColumnBuilder... columns) {
        try {
            report()
                    .addProperty(JasperProperty.EXPORT_XLS_FREEZE_ROW, "2")
                    .ignorePageWidth()
                    .ignorePagination()
                    .columns(columns)
                    .setDataSource(collection)
                    .toXlsx(getJasperXlsxExporterBuilder(fileName));
        } catch (DRException e) {
            e.printStackTrace();
        }
    }

    protected JasperXlsxExporterBuilder getJasperXlsxExporterBuilder(String fileName) {
        return export
                .xlsxExporter(fileName)
                .setDetectCellType(true)
                .setIgnorePageMargins(true)
                .setWhitePageBackground(false)
                .setRemoveEmptySpaceBetweenRows(true);
    }
}
