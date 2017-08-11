package com.zm.permissonslibrary.Permission;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.zm.permissonslibrary.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author slience
 * @des 权限请求的工具类
 * @time 2017/7/1911:52
 */

public class PermissionUtils {

    private static List<String> deniedPermission;

    // 只进行静态方法的调用，不让别人实例化对象
    private PermissionUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断版本是否是6.0以上 Marshmallow（棉花糖）
     */
    public static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 获取所有没有授予或者拒绝的权限
     *
     * @param object             Activity / Fragment
     * @param requestPermissions
     * @return
     */
    public static List<String> getDeniedPermissions(Object object, String[] requestPermissions) {
        List<String> deniedPermissions = new ArrayList<>();
        for (String requestPermission : requestPermissions) {
            if (ContextCompat.checkSelfPermission(getActivity(object), requestPermission) == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(requestPermission);
            }
        }
        return deniedPermissions;
    }

    /**
     * 获取相应的Context
     *
     * @param object
     */
    public static Activity getActivity(Object object) {
        if (object instanceof Activity) {
            return (Activity) object;
        } else if (object instanceof Fragment) {
            return ((Fragment) object).getActivity();
        }
        return null;
    }

    /**
     * 执行成功的方法
     *
     * @param mReflectObject
     * @param requestCode
     */
    static void executeSuccessMethod(Object mReflectObject, int requestCode) {
        // 获取mReflectClass中的所有方法
        Method[] declaredMethods = mReflectObject.getClass().getDeclaredMethods();
        // 进行遍历，找到打了标记的方法（请求码也要保持一致）
        for (Method method : declaredMethods) {
            //获取拥有定义标记的方法
            Log.e("Method", method + "");
            PermissionSucceed succeedMethod = method.getAnnotation(PermissionSucceed.class);
            if (succeedMethod != null) {
                //验证请求码是否保持一致
                int methodCode = succeedMethod.requestCode();
                if (methodCode == requestCode) {
                    //反射执行方法
                    Log.e("Method", "找到该方法" + method);
                    executeMethod(mReflectObject, method);
                }
            }
        }
    }

    /**
     * 反射执行方法
     */
    private static void executeMethod(Object reflectObject, Method method) {

        try {
            // 允许执行私有方法
            method.setAccessible(true);
            // 参数一：该方法属于哪一个类  参数二：传对应的参数
            method.invoke(reflectObject, new Object[]{});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断并请求剩余的未授权的权限
     *
     * @param object
     * @param mRequestPermission
     * @param requestCode
     */
    public static void requestPermissions(Object object, String[] mRequestPermission, int requestCode) {
        //获取未授权的权限
        deniedPermission = getDeniedPermissions(object, mRequestPermission);
        ActivityCompat.requestPermissions(getActivity(object),
                deniedPermission.toArray(new String[deniedPermission.size()]),
                requestCode);
    }

    /**
     * 显示权限对话框
     *
     * @param object
     * @param permissionDes
     */
    public static void showMissingPermissionDialog(final Object object, String permissionDes) {
        String formatStr = getActivity(object).getString(R.string.permission_desc_text2);
        String message = String.format(formatStr, TextUtils.isEmpty(permissionDes) ? "必要" : permissionDes);
        new AlertDialog.Builder(getActivity(object))
                .setTitle("提示")
                .setMessage(message)
                .setPositiveButton(getActivity(object).getString(R.string.settings), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings(getActivity(object));
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    /**
     * 跳转至该应用的设置页面
     *
     * @param activity
     */
    private static void startAppSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
        }
    }

    /**
     * 所有的权限都已经授权
     *
     * @param grantResults
     * @return
     */
    public static boolean hasAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否已经全部授予权限
     *
     * @param permissions
     * @return
     */
    public static boolean hasAllPermissionsGranted(Object objects, String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getActivity(objects), permission) == PackageManager.PERMISSION_DENIED)
                return false;
        }
        return true;
    }
}
