package com.zm.permissonslibrary.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author slience
 * @des
 * @time 2017/7/1912:09
 */
// Target 表示放在什么位置  METHOD：方法上面   TYPE：放在类上面   FIELD：放在属性上面
@Target(ElementType.METHOD)
// 运行时检测   SOURCE： 在程序编译时检测  RUNTIME: 在程序运行时检测
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionSucceed {
    int requestCode(); // 打标记 请求码 唯一标识
}
