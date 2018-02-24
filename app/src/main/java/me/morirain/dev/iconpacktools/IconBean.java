package me.morirain.dev.iconpacktools;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 罗 on 2018/2/22.
 */

public class IconBean {

    private static final String TAG = "IconBean";

    private static List<IconBean> iconList = new ArrayList<>();

    private boolean eff = false;

    private String iconName;

    private List<String> iconPackageName = new ArrayList<>();

    private File file;

    private static HashMap<String, IconBean> hashMap = new HashMap<String, IconBean>();


    private IconBean() {
        //Pattern finallyPattern = Pattern.compile("[^\" {/\\n]+");
        //this.iconName = name;
/*        Matcher matcher = finallyPattern.matcher(packName);
                    while (matcher.find()) {
                        matcher.find();
                        tempString = matcher.group();
                        Log.d(TAG, "start: " + tempString);
                    }*/

    }

    public static IconBean init(String name, String path) {
        //boolean exists = false;
        /*for (IconBean iconBean : iconList) {
            if (iconBean.getIconName() == name) {
                exists = true;
            }
            *//*if (hashMap.get(num) != null) {
                Integer value = hashMap.get(num);
                hashMap.put(num, value + 1);
                System.out.println("the element:" + num + " is repeat");
            } else {
                hashMap.put(num, 1);
            }*//*
        }*/
        if (hashMap.get(name) == null) {
            IconBean iconBean = new IconBean();
            iconBean.iconName = name;
            hashMap.put(name, iconBean);
            iconList.add(iconBean);
            iconBean.file = new File(path + "/" + name + ".png");
            if (!iconBean.file.exists()) {
                iconBean.eff = false;
            } else {
                iconBean.eff = true;
            }
            return iconBean;
            //Log.d(TAG, "ini2222222222222222222t: " + hashMap.get(name).getIconName());
        } else {
            return hashMap.get(name);
        }


        //return null;
    }

    public String getIconName() {
        return iconName;
    }

    public List<String> getIconPackageName() {
        return iconPackageName;
    }

    public boolean addIconPackageName(String packageName) {
        for (String pointer : iconPackageName) {
            if (pointer.equals(packageName)) {
                return false;
            }
        }
        this.iconPackageName.add(packageName);
        //Log.d(TAG, "addIconPackageName: " + packageName);
        //Log.d(TAG, ": " + this.getIconName());
        return true;
    }

    public static List<IconBean> getIconList() {
        return iconList;
    }

    public static void clear() {
        hashMap.clear();
        iconList.clear();
    }

    public File getFile() {
        return file;
    }

    public boolean isEff() {
        return eff;
    }
}
