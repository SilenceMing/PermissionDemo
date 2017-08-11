package com.zm.permissonslibrary.Permission;

import android.app.Activity;
import android.app.Fragment;

/**
 * @author slience
 * @des PermissionHelper
 * @time 2017/7/19 11:29
 */

public class PermissionHelper {

    private static Object mObject;              // 需要传入的Activity Fragment
    private static int mRequestCode;            // 请求码
    private String[] mRequestPermission; // 需要请求的权限集合
    private static String mPermissionDes;       // 需要显示的权限说明

    public PermissionHelper(Object object) {
        this.mObject = object;
    }

    /***********************************利用链式传递权限参数******************************************/
    public static PermissionHelper with(Activity activity) {
        return new PermissionHelper(activity);
    }

    public static PermissionHelper with(Fragment fragment) {
        return new PermissionHelper(fragment);
    }

    public PermissionHelper requestCode(int requestCode) {
        this.mRequestCode = requestCode;
        return this;
    }

    public PermissionHelper setPermissionDes(String permissionDes) {
        this.mPermissionDes = permissionDes;
        return this;
    }

    public PermissionHelper requestPermissions(String... requestPermission) {
        this.mRequestPermission = requestPermission;
        return this;
    }
    /***********************************利用链式传递权限参数******************************************/
    /**
     * 权限的判断和请求的发送
     */
    public void request() {
        // 1.判断是否是6.0版本以上
        if (!PermissionUtils.isOverMarshmallow()) {
            // 2.如果不是6.0以上，直接通过反射获取执行方法，执行方法
            PermissionUtils.executeSuccessMethod(mObject, mRequestCode);
            return;
        } else {
            // 3.如果是6.0 以上，首先需要判断权限是否已经授予
            //执行什么方法并不确定，只能通过注解的方式给方法打一个标记
            //通过反射去执行  注解+反射
            //获取没有授予权限的列表
            if (PermissionUtils.hasAllPermissionsGranted(mObject,mRequestPermission)) {
                // 3.1. 授予：通过反射获取方法并执行
                PermissionUtils.executeSuccessMethod(mObject,mRequestCode);
            } else {
                // 3.2. 没有全部授予： 申请权限
                PermissionUtils.requestPermissions(mObject,mRequestPermission, mRequestCode);
            }
        }
    }

    /**
     * 处理权限申请的回调
     * @param object
     * @param requestCode
     * @param grantResults
     */
    public static void requestPermissionResult(Object object, int requestCode, int[] grantResults) {
        if (requestCode == mRequestCode){
            if(PermissionUtils.hasAllPermissionsGranted(grantResults)){
                //权限全部授予 执行方法
                PermissionUtils.executeSuccessMethod(object, requestCode);
            }else{
                //权限没有全部授予，再次请求权限
                PermissionUtils.showMissingPermissionDialog(mObject,mPermissionDes);
            }
        }
    }
}
