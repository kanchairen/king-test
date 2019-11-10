package com.lky.modules.api;

import com.lky.service.UserService;
import org.junit.Test;

import javax.inject.Inject;

/**
 * ${DESCRIPTION}
 *
 * @author luckyhua
 * @version 1.0
 * @since 2018/4/2
 */
public class RabbitTest extends BaseTest {

    @Inject
    private UserService userService;

    @Test
    public void register() {
        long startTime = System.currentTimeMillis();
        System.out.println("----------------" + (System.currentTimeMillis() - startTime) + "ms");
    }

}
