package me.morirain.dev.iconpacktools;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class LicenseActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);
        Toolbar toolbar = findViewById(R.id.license_toolbar);
        CardView licenseGlide = findViewById(R.id.cardViewGlideAuthor);
        CardView licenseGlideT = findViewById(R.id.cardViewGlidetAuthor);
        CardView licenseLFilePicker = findViewById(R.id.cardViewLFilePickerAuthor);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        licenseGlide.setOnClickListener(this);
        licenseGlideT.setOnClickListener(this);
        licenseLFilePicker.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.cardViewGlideAuthor:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/bumptech/glide"));
                startActivity(intent);
                break;
            case R.id.cardViewGlidetAuthor:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/wasabeef/glide-transformations"));
                startActivity(intent);
                break;
            case R.id.cardViewLFilePickerAuthor:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/leonHua/LFilePicker"));
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
