package com.lky.scheduling;

import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.DateUtils;
import com.lky.dao.ProductGroupDao;
import com.lky.entity.Product;
import com.lky.entity.ProductGroup;
import com.lky.service.ProductGroupService;
import com.lky.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 商品处理
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/6
 */
@Component
public class ProductScheduling {

    private static final Logger log = LoggerFactory.getLogger(ProductScheduling.class);

    @Inject
    private ProductGroupService productGroupService;

    @Inject
    private ProductGroupDao productGroupDao;

    @Inject
    private ProductService productService;


    /**
     * 统计所有商品组近3个月的销售数量
     */
    @Scheduled(cron = "10 0 * * * ?")
    private void updateProductGroupRecentSaleNum() {

        Date threeMothBefore = DateUtils.add(new Date(), Calendar.MONTH, -3);
        //连接查询近3个月所有支付订单的商品组id和购买数量
        List<Object[]> recentDataList = productGroupDao.findGroupIdAndSaleNumber(threeMothBefore);

        //更新所有商品组的近期销量
        List<ProductGroup> productGroupList = productGroupService.findAll();
        if (!CollectionUtils.isEmpty(productGroupList)) {
            List<ProductGroup> changeGroupList = new ArrayList<>();
            for (ProductGroup productGroup : productGroupList) {
                int recentSaleNum = 0;
                if (!CollectionUtils.isEmpty(recentDataList)) {
                    for (Object[] params : recentDataList) {
                        if ((int) params[0] == productGroup.getId()) {
                            recentSaleNum += (int) params[1];
                        }
                    }
                }
                if (productGroup.getRecentSold() != recentSaleNum) {
                    productGroup.setRecentSold(recentSaleNum);
                    changeGroupList.add(productGroup);
                }
            }
            productGroupService.update(changeGroupList);
        }
    }


    /**
     * 统计所有商品和商品组的月销售数量
     */
    @Scheduled(cron = "0 8 * * * ?")
    private void updateProductMonthSaleNum() {

        Date oneMonthBefore = DateUtils.add(new Date(), Calendar.MONTH, -1);
        //获取每个有销量商品组的月销量
        List<Object[]> monthGroupList = productGroupDao.findMonthGroupSale(oneMonthBefore);

////        连接查询近1个月所有支付订单的商品组id和商品id和购买数量
//        List<Object[]> monthDataList = productGroupDao.findGroupIdAndProductIdAndSaleNumber(oneMonthBefore);

        //更新商品组的月销量
        List<ProductGroup> groupList = productGroupService.findAll();
        if (!CollectionUtils.isEmpty(groupList)) {
            List<ProductGroup> changeGroup = new ArrayList<>();
            for (ProductGroup productGroup : groupList) {
                Boolean haveSale = Boolean.FALSE; //判断当月是否有销量
                if (!CollectionUtils.isEmpty(monthGroupList)) {
                    for (Object[] objects : monthGroupList) {
                        if ((int) objects[0] == productGroup.getId()) {
                            haveSale = Boolean.TRUE;
                            int number = Integer.parseInt(objects[1].toString());
                            if (productGroup.getMonthSold() != number) {
                                productGroup.setMonthSold(number);
                                changeGroup.add(productGroup);
                                break;
                            }
                        }
                    }
                }
                //如果商品组的当月销量为0且数据库中不为0，则更改月销量为0
                if (!haveSale && productGroup.getMonthSold() != 0) {
                    productGroup.setMonthSold(0);
                    changeGroup.add(productGroup);
                }
            }
            productGroupService.update(changeGroup);
        }

        //更新商品的月销量
        //获取每个有销量商品的月销量
        List<Object[]> monthProductList = productGroupDao.findMonthProductSale(oneMonthBefore);
        List<Product> productList = productService.findAll();

        if (!CollectionUtils.isEmpty(productList)) {
            List<Product> changeProduct = new ArrayList<>();
            for (Product product : productList) {
                Boolean haveSale = Boolean.FALSE; //判断当月是否有销量
                if (!CollectionUtils.isEmpty(monthProductList)) {
                    for (Object[] objects : monthProductList) {
                        if ((int) objects[0] == product.getId()) {
                            haveSale = Boolean.TRUE;
                            int number = Integer.parseInt(objects[1].toString());
                            if (product.getMonthSold() != number) {
                                product.setMonthSold(number);
                                changeProduct.add(product);
                                break;
                            }
                        }
                    }
                }
                //如果商品的当月销量为0且数据库中不为0，则更改月销量为0
                if (!haveSale && product.getMonthSold() != 0) {
                    product.setMonthSold(0);
                    changeProduct.add(product);
                }
            }
            productService.update(changeProduct);
        }

    }

}
