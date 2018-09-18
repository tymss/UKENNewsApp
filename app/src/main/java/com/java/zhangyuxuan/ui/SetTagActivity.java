package com.java.zhangyuxuan.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.java.zhangyuxuan.R;
import com.java.zhangyuxuan.utils.TagUtil;

import java.util.ArrayList;

public class SetTagActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ArrayList<CheckBox> boxes;
    private String[] tags = TagUtil.tag;
    private Button confirm;

    @Override
    protected void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_gettag);

        initToolbar();
        getBox();
        setBox();
        setButton();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.gettag_toolbar);
        toolbar.setTitle("请选择您喜欢的标签");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getBox()
    {
        boxes = new ArrayList<>();
        boxes.add((CheckBox)findViewById(R.id.b_REC));
        boxes.add((CheckBox)findViewById(R.id.b_GJ));
        boxes.add((CheckBox)findViewById(R.id.b_SH));
        boxes.add((CheckBox)findViewById(R.id.b_DY));
        boxes.add((CheckBox)findViewById(R.id.b_DS));
        boxes.add((CheckBox)findViewById(R.id.b_MX));
        boxes.add((CheckBox)findViewById(R.id.b_YY));
        boxes.add((CheckBox)findViewById(R.id.b_ZQ));
        boxes.add((CheckBox)findViewById(R.id.b_TY));
        boxes.add((CheckBox)findViewById(R.id.b_YX));
        boxes.add((CheckBox)findViewById(R.id.b_DM));
        boxes.add((CheckBox)findViewById(R.id.b_XZ));
    }

    private void setBox()
    {
        SharedPreferences shared = getSharedPreferences("tag", MODE_PRIVATE);
        for(int i = 0; i < 12; i++)
            boxes.get(i).setChecked(shared.getBoolean(tags[i], true));
    }

    private void setButton()
    {
        confirm = (Button)findViewById(R.id.confirm_button);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences shared = getSharedPreferences("tag", MODE_PRIVATE);
                Editor editor = shared.edit();
                for(int i = 0; i < 12; i++)
                    editor.putBoolean(tags[i], boxes.get(i).isChecked());
                editor.commit();
                Intent intent = new Intent(SetTagActivity.this, MainActivity.class);
                intent.putExtra("setTag", true);
                startActivity(intent);
                finish();
            }
        });
    }
}
