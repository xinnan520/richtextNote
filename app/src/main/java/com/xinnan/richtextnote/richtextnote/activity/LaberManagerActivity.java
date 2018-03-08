package com.xinnan.richtextnote.richtextnote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xinnan.richtextnote.richtextnote.R;
import com.xinnan.richtextnote.richtextnote.bean.Group;
import com.xinnan.richtextnote.richtextnote.db.GroupDao;
import com.xinnan.richtextnote.richtextnote.view.FlowLayout;

import java.util.ArrayList;
import java.util.List;

public class LaberManagerActivity extends BaseActivity {
    ArrayList<String> list;
    LayoutInflater mInflater;
    private FlowLayout mFlowLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laber_manager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_laber);
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
        list = new ArrayList<>();

        GroupDao groupDao = new GroupDao(LaberManagerActivity.this);
        List<Group> groupList = groupDao.queryGroupAll();
        for (int i = 0; i < groupList.size(); i++) {
            list.add(groupList.get(i).getName());
        }
//        list.add("糗事");
//        list.add("里约奥运会");
//        list.add("菲尔普斯");
//        list.add("吐槽");
//        list.add("王宝强离婚");
//        list.add("洪荒少女傅园慧");
//        list.add("嗑药骗子霍顿");


        mInflater = LayoutInflater.from(this);
        updateData();

    }

    private void updateData() {

        mFlowLayout.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            TextView view = (TextView) mInflater.inflate(R.layout.item_flow, mFlowLayout, false);
            view.setText(list.get(i));
            mFlowLayout.addView(view);


        }

        ImageView iv = (ImageView) mInflater.inflate(R.layout.add_item_flow, mFlowLayout, false);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(LaberManagerActivity.this, LaberAddActivity.class).putStringArrayListExtra("bq", list), 1);
            }
        });
        mFlowLayout.addView(iv);

    }

}
