package com.xinnan.richtextnote.richtextnote.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xinnan.richtextnote.richtextnote.R;
import com.xinnan.richtextnote.richtextnote.db.GroupDao;
import com.xinnan.richtextnote.richtextnote.view.FlowLayout;

import java.util.ArrayList;

public class LaberAddActivity extends BaseActivity {
    ArrayList<String> list;
    EditText et;
    LayoutInflater mInflater;
    private FlowLayout mFlowLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laber_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_laber_add);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //toolbar.setNavigationIcon(R.drawable.ic_dialog_info);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mFlowLayout = (FlowLayout) findViewById(R.id.id_flowlayout);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = et.getText().toString();
                if (!str.equals("")) {
                    list.add(str);
                    GroupDao groupDao = new GroupDao(LaberAddActivity.this);
                    groupDao.insertGroup(str);
                }
                setResult(1, new Intent(LaberAddActivity.this, MainActivity.class).putStringArrayListExtra("bg", list));
                finish();
            }
        });
        list = getIntent().getStringArrayListExtra("bq");
        mInflater = LayoutInflater.from(this);
        initData();
    }

    private void initData() {
        mFlowLayout.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            LinearLayout view = (LinearLayout) mInflater.inflate(R.layout.add_tv_item_flow, mFlowLayout, false);
            final TextView tv = (TextView) view.findViewById(R.id.tv);
            final int finalI = i;
            view.findViewById(R.id.iv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFlowLayout.removeViewAt(finalI);
                    list.remove(finalI);
                    GroupDao groupDao = new GroupDao(LaberAddActivity.this);
                    groupDao.deleteGroup(tv.getText().toString());
                    initData();

                }
            });
            tv.setText(list.get(i));
            mFlowLayout.addView(view);
        }
        et = (EditText) mInflater.inflate(R.layout.edit_item_flow, mFlowLayout, false);
        mFlowLayout.addView(et);

    }
}
