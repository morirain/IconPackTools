package me.morirain.dev.iconpacktools;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final int SET_DIR = 10;

    private final int START = 20;

    private int taskState = SET_DIR;

    private String path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //shi代码……
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CardView licenseCard = findViewById(R.id.cardViewLicense);
        CardView authorCard = findViewById(R.id.cardViewAuthor);
        CardView coolapkCard = findViewById(R.id.cardViewCoolapk);
        CardView aliCard = findViewById(R.id.cardViewAli);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        ImageView titleImage = findViewById(R.id.title_image);
        ImageView authorImage = findViewById(R.id.author_image);
        int rd = (int) (Math.random() * 100);
        if (rd >= 50) {
            Glide.with(this).load(R.drawable.xsx13).into(titleImage);
        } else {
            Glide.with(this).load(R.drawable.xsx12).into(titleImage);
        }
        Glide.with(this).load(R.drawable.sy).apply(bitmapTransform(new CircleCrop())).into(authorImage);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
        }

        fab.setOnClickListener(this);
        licenseCard.setOnClickListener(this);
        authorCard.setOnClickListener(this);
        coolapkCard.setOnClickListener(this);
        aliCard.setOnClickListener(this);
    }

    private void setDirectory(int code, String title) {
        new LFilePicker()
                    .withActivity(MainActivity.this)
                    .withRequestCode(code)
                    .withStartPath("/storage/emulated/0")//指定初始显示路径
                    .withChooseMode(false)
                    .withBackIcon(Constant.BACKICON_STYLETHREE)
                    .withBackgroundColor("#3F51B5")
                    .withTitle(title).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SET_DIR) {
                path = data.getStringExtra("path");
                taskState = START;

                if (!GetStart.getInstance().check(path)) {
                    Toast.makeText(MainActivity.this, "这个目录下检测不到 appfilter 文件", Toast.LENGTH_SHORT).show();
                    path = "";
                    taskState = SET_DIR;
                } else {
                    Toast.makeText(MainActivity.this, "选中的路径为" + path, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.fab:
                if (taskState == SET_DIR) {
                    setDirectory(SET_DIR, "选择图标目录");
                    Toast.makeText(MainActivity.this, "选择图标文件所在目录", Toast.LENGTH_SHORT).show();
                } else if (taskState == START && !(path == null || path.isEmpty()) && !path.equals("")) {

                    if (!GetStart.getInstance().init(path, IconBean.getIconList(), MainActivity.this)) {
                        Toast.makeText(MainActivity.this, "出现未知错误", Toast.LENGTH_SHORT).show();
                    }
                    path = "";
                    taskState = SET_DIR;
                } else {
                    setDirectory(SET_DIR, "选择图标目录");
                    Toast.makeText(MainActivity.this, "选择图标文件所在目录", Toast.LENGTH_SHORT).show();
                    taskState = SET_DIR;

                }
                break;
            case R.id.cardViewLicense:
                intent = new Intent(this, LicenseActivity.class);
                startActivity(intent);
                break;
            case R.id.cardViewAuthor:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://weibo.cn/qr/userinfo?uid=5447517178"));
                startActivity(intent);
                break;
            case R.id.cardViewCoolapk:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.coolapk.com/u/533268"));
                startActivity(intent);
                break;
            case R.id.cardViewAli:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://render.alipay.com/p/f/fd-j6lzqrgm/guiderofmklvtvw.html?shareId=2088722353860642&campStr=p1j%2BdzkZl018zOczaHT4Z5CLdPVCgrEXq89JsWOx1gdt05SIDMPg3PTxZbdPw9dL&sign=Awt2cbL%2FJihSCgBO9C1pDkAglkLrpC6CdSkx8wn%2BUwQ%3D&scene=offlinePaymentNewSns"));
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
