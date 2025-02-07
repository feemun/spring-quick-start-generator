package com.wenka.mdsc.generator.annotation;

import java.lang.annotation.*;

/**
 * Created with IDEA
 *
 * @author wenka wkwenka@gmail.com
 * @date 2020/03/25  上午 10:00
 * @description: 容器注入注解：字段值注入(读取配置文件值)
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Value {

    String value();

}
