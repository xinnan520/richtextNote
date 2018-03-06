package com.xinnan.richtextnote.richtextnote.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.xinnan.richtextnote.richtextnote.R;
import com.xinnan.richtextnote.richtextnote.adapter.MyNoteListAdapter;
import com.xinnan.richtextnote.richtextnote.bean.Note;
import com.xinnan.richtextnote.richtextnote.db.NoteDao;
import com.xinnan.richtextnote.richtextnote.view.SpacesItemDecoration;

import java.util.List;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private XRecyclerView rv_list_main;
    private MyNoteListAdapter mNoteListAdapter;
    private List<Note> noteList;
    private NoteDao noteDao;
    private int groupId;//分类ID
    private String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this, NewActivity.class);
                intent.putExtra("groupName", groupName);
                intent.putExtra("flag", 0);
                startActivity(intent);
            }
        });

        noteDao = new NoteDao(this);

        rv_list_main = (XRecyclerView) findViewById(R.id.rv_list_main);
        /****************** 设置XRecyclerView属性 **************************/
        rv_list_main.addItemDecoration(new SpacesItemDecoration(5));//设置item间距
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//竖向列表
        rv_list_main.setLayoutManager(layoutManager);

        rv_list_main.setLoadingMoreEnabled(true);//开启上拉加载
        rv_list_main.setPullRefreshEnabled(true);//开启下拉刷新
        rv_list_main.setRefreshProgressStyle(ProgressStyle.SquareSpin);
        rv_list_main.setLoadingMoreProgressStyle(ProgressStyle.BallScale);
        /****************** 设置XRecyclerView属性 **************************/

        mNoteListAdapter = new MyNoteListAdapter();
        mNoteListAdapter.setmNotes(noteList);
        rv_list_main.setAdapter(mNoteListAdapter);

        rv_list_main.setLoadingListener(new MainActivity.MyLoadingListener());
        mNoteListAdapter.setOnItemClickListener(new MyNoteListAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, Note note) {
                showToast(note.getTitle());

                Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("note", note);
                intent.putExtra("data", bundle);
                startActivity(intent);
            }
        });
        mNoteListAdapter.setOnItemLongClickListener(new MyNoteListAdapter.OnRecyclerViewItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, final Note note) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                builder.setTitle("提示");
//                builder.setMessage("确定删除笔记？");
//                builder.setCancelable(false);
//                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        int ret = noteDao.deleteNote(note.getId());
//                        if (ret > 0){
//                            showToast("删除成功");
//                            refreshNoteList();
//                        }
//                    }
//                });
//                builder.setNegativeButton("取消", null);
//                builder.create().show();
                Snackbar.make(view, "确定删除笔记？右滑取消", Snackbar.LENGTH_LONG)
                        .setAction("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int ret = noteDao.deleteNote(note.getId());
                                if (ret > 0){
                                    showToast("删除成功");
                                    refreshNoteList();
                                }
                            }
                        }).show();
            }
        });

    }

    /** 上拉加载和下拉刷新事件 **/
    private class MyLoadingListener implements XRecyclerView.LoadingListener{

        @Override
        public void onRefresh() {//下拉刷新
            rv_list_main.postDelayed(new Runnable() {
                @Override
                public void run() {
                    rv_list_main.refreshComplete();
                }
            }, 1000);
        }

        @Override
        public void onLoadMore() {//上拉加载
            rv_list_main.postDelayed(new Runnable() {
                @Override
                public void run() {
                    rv_list_main.loadMoreComplete();
                }
            }, 1000);
        }
    }

    //刷新笔记列表
    private void refreshNoteList(){
        noteList = noteDao.queryNotesAll(groupId);
        //Log.i(TAG, "###noteList: "+noteList);
        mNoteListAdapter.setmNotes(noteList);
        mNoteListAdapter.notifyDataSetChanged();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        getMenuInflater().inflate(R.menu.search,menu);
        //获取搜索的菜单组件
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                noteList = noteDao.queryNoteByKeyword(query);
                //Log.i(TAG, "###noteList: "+noteList);
                mNoteListAdapter.setmNotes(noteList);
                mNoteListAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                noteList = noteDao.queryNoteByKeyword(query);
                //Log.i(TAG, "###noteList: "+noteList);
                mNoteListAdapter.setmNotes(noteList);
                mNoteListAdapter.notifyDataSetChanged();
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshNoteList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
