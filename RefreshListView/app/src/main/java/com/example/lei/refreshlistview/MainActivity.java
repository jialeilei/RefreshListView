package com.example.lei.refreshlistview;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;

/**
 * 下拉刷新 listView
 */
public class MainActivity extends AppCompatActivity {

    RefreshListView mRefreshListView;
    List<String> mData = new ArrayList<>();
    private ListViewAdapter mAdapter = null;
    private Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initEvent();
    }

    private void initView() {

        mRefreshListView = (RefreshListView) findViewById(R.id.refresh);

    }

    private void initEvent() {

        initData();

        mHandler = new Handler();

        showList();

        mRefreshListView.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void refresh() {

                //刷新数据

                updateData();

                //更新listView

                showList();

                //停止刷新

                /*
                * 模拟沉睡2秒
                * */
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshListView.refreshComplete();
                    }
                },2000);

            }
        });

    }

    private void showList() {
        if (mAdapter == null){
            mAdapter = new ListViewAdapter(this,mData,R.layout.listview_item);
            mRefreshListView.setAdapter(mAdapter);
        }else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void initData() {
        for (int i = 0; i < 20; i++) {
            mData.add(" "+i);
        }
    }


    private void updateData() {
        for (int i = 0; i < 2; i++) {
            mData.add(0,"刷新的数据： "+i);
        }
    }
}
