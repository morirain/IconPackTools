package me.morirain.dev.iconpacktools;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

    private static final Pattern filePattern = Pattern.compile("\"[_a-z0-9]+\"");
    private static final Pattern packagePattern = Pattern.compile("\\{[\\S]+/");
    private static final Pattern finallyPattern = Pattern.compile("[^\" {/\\n]+");
    private static final Pattern endPattern = Pattern.compile("<iconback");

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
        //copyIconFile();
        dialog = new ProgressDialog(context);
        dialog.setTitle("任务进行中");
        //dialog.setMessage("in progress...");
        dialog.setCancelable(false);
        StartTask task = new StartTask();
        task.execute(list);
        IconBean.clear();
        //progressDialog.dismiss();
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


    private boolean copyIconFile() {

        return true;
    }

    class StartTask extends AsyncTask<List<IconBean>, Integer, Boolean> {

        private boolean start() {
            Matcher matcher;
            String tempString;
            IconBean iconBean = null;
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(appFilter));
                String buffer;
                while ((buffer = reader.readLine()) != null) {
                    if (!isEmpty(buffer)) {

                        tempString = "";
                        //检测是否已结束
                        matcher = endPattern.matcher(buffer);
                        while (matcher.find()) {
                            tempString = matcher.group();
                        }
                        if (tempString.equals("<iconback")) {
                            reader.close();
                            break;
                        }
                        // 读取文件名
                        matcher = filePattern.matcher(buffer);
                        while (matcher.find()) {
                            tempString = matcher.group();
                        }
                        matcher = finallyPattern.matcher(tempString);
                        while (matcher.find()) {
                            tempString = matcher.group();
                            //Log.d(TAG, "start: " + tempString);
                        }
                        if (!isEmpty(tempString)) {
                            //iconList.add(
                            iconBean = IconBean.init(tempString, path);
                            //Log.d(TAG, "start: 2222222222gggggggggggggggggsdggggggggggggggggggg2222222222221" + );
                            //);
                        }
                        tempString = "";

                        //读取包名
                        matcher = packagePattern.matcher(buffer);
                        while (matcher.find()) {
                            tempString = matcher.group();
                        }
                        matcher = finallyPattern.matcher(tempString);
                        while (matcher.find()) {
                            tempString = matcher.group();
                            //Log.d(TAG, "start: " + tempString);
                        }
                        if ((!isEmpty(tempString)) && iconBean != null) {
                            //iconList.add(
                            iconBean.addIconPackageName(tempString);
                            //Log.d(TAG, "start: 2222222222gggggggggggggggggsdggggggggggggggggggg2222222222221" + );
                            //);
                        }
                        iconBean = null;

                    }
                    //sbData.append(buffer).append("\n");
                }
                //if (sbData.length() > 0) {
                //sbData.setLength(sbData.length() - 1);
                //}

                //return sbData.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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
                                //Log.d(TAG, "copyIconFile: " + file.toString());
                                if (file.exists()) {
                                    continue;
                                }
                                fosto = new RandomAccessFile(file, "rw");
                                //marked = true;
                                //fosfrom.mark(Integer.MAX_VALUE);
                                //Log.d(TAG, "copyIconFile: "+fosto.toString());
                                byte bt[] = new byte[4096];
                                int c;
                                while ((c = fosfrom.read(bt)) > 0) {
                                    fosto.write(bt, 0, c);
                            /*if(fosfrom.available() < 1024) { // 剩余的数据比1024字节少，一位一位读出再写入目的文件
                                c = -1;
                                while((c = fosfrom.read()) != -1) {
                                    fosto.write(c);
                                }
                            }*/
                                }
                                //fosto.flush();
                                fosfrom.seek(0);
                                fosto.close();
                                fosto = null;


                                //fosfrom = null;
                                //fosto = null;
                                //fosfrom.reset();
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
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (fosto != null) {
                            fosto.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                //return false;
                return true;

        }



        /*@Override
        protected Boolean doInBackground(List<IconBean>[] lists) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (doInTask(lists)) {
                return true;
            }
                File newDir;
                String dirName = path + "/" + "NewIcon";
                BufferedInputStream fosfrom = null;
                OutputStream fosto = null;
                File file;
                List<IconBean> list = lists[0];
                if (!start()) {
                    return false;
                }
                Double number = (double)list.size();
                Double proNum = (double) 0;
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
                    *//*file = iconBean.getFile();
                    if (!file.exists()) {
                        continue;
                    }*//*
                            //fosfrom = new FileInputStream(iconBean.getFile());

                            for (String packageName : iconBean.getIconPackageName()) {
                                fosfrom = new BufferedInputStream(new FileInputStream(iconBean.getFile()));
                                //这里的 packageName 必定不会重复
                                file = new File(newDir + "/" + packageName + ".png");
                                Log.d(TAG, "copyIconFile: " + file.toString());
                                if (file.exists()) {
                                    continue;
                                }
                                fosto = new FileOutputStream(file);
                                //marked = true;
                                //fosfrom.mark(Integer.MAX_VALUE);
                                //Log.d(TAG, "copyIconFile: "+fosto.toString());
                                byte bt[] = new byte[2048];
                                int c;
                                while ((c = fosfrom.read(bt)) > 0) {
                                    fosto.write(bt, 0, c);
                            *//*if(fosfrom.available() < 1024) { // 剩余的数据比1024字节少，一位一位读出再写入目的文件
                                c = -1;
                                while((c = fosfrom.read()) != -1) {
                                    fosto.write(c);
                                }
                            }*//*
                                }
                                fosto.flush();
                                fosfrom.close();
                                fosto.close();
                                fosfrom = null;
                                fosto = null;
                                //fosfrom.reset();
                            }
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
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (fosto != null) {
                            fosto.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return true;
        }*/

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
