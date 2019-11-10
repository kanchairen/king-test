package com.lky.modules.api;

import com.lky.LkyApplication;
import com.lky.modules.sys.controller.SHighConfigController;
import com.lky.scheduling.WpointConvertScheduling;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

/**
 * 乐康指数计算测试类
 *
 * @author zhangzheng
 * @version 1.0
 * @since 17-11-17
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LkyApplication.class)
public class LHealthIndexTest {

    @Inject
    private WpointConvertScheduling wpointConvertScheduling;

    @Inject
    private SHighConfigController sHighConfigController;

    @Test
    public void lhealthIndex() {
        //计算乐康指数
        wpointConvertScheduling.computeLHeathIndex();
        System.out.println("dafa");
    }

    @Test
    public void transfer() {
        //立即转换G米
        sHighConfigController.immediateTransfer();
    }
}
