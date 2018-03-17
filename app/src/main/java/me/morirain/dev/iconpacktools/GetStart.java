package me.morirain.dev.iconpacktools;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 罗 on 2018/2/22.
 */

public class GetStart {

    private static final Pattern componentPattern = Pattern.compile("ComponentInfo\\{([^/]+?)/(.+?)\\}");

    private ProgressDialog dialog;

    private static final String TAG = "GetStart";

    private String path;

    private File appFilter;

    private static GetStart instance;

    private GetStart(){}

    public static GetStart getInstance() {
        if (instance == null) {
            instance = new GetStart();
        }
        return instance;
    }

    private boolean isEmpty(String s) {
        if ((s.isEmpty() || s.equals(""))) {
            return true;
        }
        return false;
    }

    public boolean init(String path, List<IconBean> list, Context context) {
        if (appFilter == null) {
            return false;
        }
        this.path = path;
        dialog = new ProgressDialog(context);
        dialog.setTitle("任务进行中");
        dialog.setCancelable(false);
        StartTask task = new StartTask();
        task.execute(list);
        IconBean.clear();
        return true;
    }

    public boolean check(String path) {
        try {
            File file = new File(path + "/appfilter.xml");
            if (!file.exists()) {
                file = new File(path + "/appfilter.txt");
                if (!file.exists()) {
                    return false;
                }
            }
            this.path = path;
            appFilter = file;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    class StartTask extends AsyncTask<List<IconBean>, Integer, Boolean> {

        private boolean start() {
            Matcher matcher;
            IconBean iconBean;

            try {
                FileInputStream fis = new FileInputStream(appFilter);
                // 获得pull解析器对象
                XmlPullParser parser = Xml.newPullParser();
                // 指定解析的文件和编码格式
                parser.setInput(fis, "utf-8");
                int event = parser.getEventType();// 获得事件类型
                while (event != XmlPullParser.END_DOCUMENT) {
                    if (event == XmlPullParser.START_TAG) {
                        if (!"item".equals(parser.getName())) {
                            event = parser.next();
                            continue;
                        }
                        String drawable = parser.getAttributeValue(null, "drawable");
                        if (TextUtils.isEmpty(drawable)) {
                            event = parser.next();
                            continue;
                        }
                        String component = parser.getAttributeValue(null, "component");
                        if (TextUtils.isEmpty(component)) {
                            event = parser.next();
                            continue;
                        }
                        matcher = componentPattern.matcher(component);
                        if (!matcher.matches()) {
                            event = parser.next();
                            continue;
                        }
                        iconBean = IconBean.init(drawable, path);
                        iconBean.addIconPackageName(matcher.group(1));
                    }
                    event = parser.next();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }


        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(List<IconBean>[] lists) {
                File newDir;
                String dirName = path + "/" + "NewIcon";
                RandomAccessFile fosfrom = null;
                RandomAccessFile fosto = null;
                File file;
                List<IconBean> list = lists[0];
                if (!start()) {
                    return false;
                }
                Double number = (double)list.size();
                Double proNum = (double)0;
                try {
                    while (true) {
                        newDir = new File(dirName);
                        if (newDir.exists()) {
                            dirName = dirName + "_new";
                        } else {
                            newDir.mkdir();
                            break;
                        }
                    }
                    for (IconBean iconBean : list) {
                        if (iconBean.isEff()) {
                    /*file = iconBean.getFile();
                    if (!file.exists()) {
                        continue;
                    }*/
                            //fosfrom = new FileInputStream(iconBean.getFile());
                            fosfrom = new RandomAccessFile(iconBean.getFile(), "r");
                            for (String packageName : iconBean.getIconPackageName()) {
                                //这里的 packageName 必定不会重复
                                file = new File(newDir + "/" + packageName + ".png");
                                if (file.exists()) {
                                    continue;
                                }
                                fosto = new RandomAccessFile(file, "rw");
                                byte bt[] = new byte[4096];
                                int c;
                                while ((c = fosfrom.read(bt)) > 0) {
                                    fosto.write(bt, 0, c);
                                }
                                fosfrom.seek(0);
                                fosto.close();
                                fosto = null;
                            }
                            fosfrom.close();
                            fosfrom = null;
                        }
                        proNum += 1;
                        publishProgress((int) ((proNum / number) * 100.0));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (fosfrom != null) {
                            fosfrom.close();
                        }
                        if (fosto != null) {
                            fosto.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                return true;

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            dialog.setMessage("in progress...  " + values[0] + "%");
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            dialog.cancel();
            Toast.makeText(BaseApplicaton.getContext(), "Complete", Toast.LENGTH_SHORT).show();
        }
    }

}
