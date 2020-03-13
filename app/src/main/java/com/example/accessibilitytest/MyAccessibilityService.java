package com.example.accessibilitytest;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yyz (杨云召)
 * @date 2019/4/22
 * time   16:23
 * description
 */
public class MyAccessibilityService extends AccessibilityService {
    private static final String TAG = "Accessibility";
    List<String> stringList = new ArrayList<>();

    {
        stringList.add("继续安装");
        stringList.add("立即开始");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        String pkgName = event.getPackageName().toString();
        Log.i(TAG, "onAccessibilityEvent: " + pkgName);
        if ("com.android.systemui".equals(pkgName) || "com.miui.securitycenter".equals(pkgName)) {
            performClick(findViewByEqualsText());
        }
    }

    @Override
    public void onInterrupt() {

    }

    /**
     * 根据getRootInActiveWindow查找和当前text相等的控件
     */
    @Nullable
    public List<AccessibilityNodeInfo> findViewByEqualsText() {
        List<AccessibilityNodeInfo> listOld = findViewByContainsText();
        if (listOld == null || listOld.isEmpty()) {
            return null;
        }
        ArrayList<AccessibilityNodeInfo> listNew = new ArrayList<>();
        for (AccessibilityNodeInfo ani : listOld) {
            if (ani.getText() != null) {
                String s = ani.getText().toString();
                if (stringList.get(0).equals(s) || stringList.get(1).equals(s)) {
                    listNew.add(ani);
                } else {
                    ani.recycle();
                }
            }
        }
        return listNew;
    }

    /**
     * 根据getRootInActiveWindow查找包含当前text的控件
     */
    @Nullable
    public List<AccessibilityNodeInfo> findViewByContainsText() {
        AccessibilityNodeInfo info = getRootInActiveWindow();
        if (info == null) return null;
        List<AccessibilityNodeInfo> list = new ArrayList<>();
        for (String s : stringList) {
            list.addAll(info.findAccessibilityNodeInfosByText(s));
        }
        info.recycle();
        return list;
    }

    private void performClick(List<AccessibilityNodeInfo> nodeInfos) {
        if (nodeInfos != null && !nodeInfos.isEmpty()) {
            AccessibilityNodeInfo node;
            for (int i = 0; i < nodeInfos.size(); i++) {
                node = nodeInfos.get(i);
                // 获得点击View的类型
                // 进行模拟点击
                if (node.isEnabled()) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return;
                }
            }
        }
    }
}
