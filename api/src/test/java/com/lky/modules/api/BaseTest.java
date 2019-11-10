package com.lky.modules.api;

import com.lky.LkyApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 测试基类
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/10
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = {"classpath:application-dev.yml"})
@SpringBootTest(classes = LkyApplication.class)
public abstract class BaseTest {
}
