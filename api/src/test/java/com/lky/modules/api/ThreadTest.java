package com.lky.modules.api;

import com.lky.service.UserAssetService;

import javax.inject.Inject;

/**
 * ${DESCRIPTION}
 *
 * @author luckyhua
 * @version 1.0
 * @since 2018/3/21
 */
public class ThreadTest extends BaseTest {

    @Inject
    private UserAssetService userAssertService;

//    @Test
//    public void testSave() {
//        new Thread(() -> {
//            userAssertService.save(1, 100);
//        }).start();
//        new Thread(() -> {
//            userAssertService.save(1, -10);
//        }).start();
//        try {
//            Thread.sleep(5000);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
