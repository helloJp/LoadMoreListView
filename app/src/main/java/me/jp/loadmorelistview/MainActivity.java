package me.jp.loadmorelistview;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements LoadMoreListView.OnLoadMoreListener {

    LoadMoreListView mListView;
    List<String> mData = new ArrayList<>();
    private final int EACH_NUM = 15;
    private ArrayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addMoreData();
        initView();
    }

    private void addMoreData() {
        for (int i = 0; i < EACH_NUM; i++) {
            mData.add("I am item " + i);
        }
    }

    private void initView() {
        mListView = (LoadMoreListView) findViewById(R.id.lv_load_more);
        mAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mData);
        mListView.setAdapter(mAdapter);
        mListView.setOnLoadMoreListener(this);

        if (mData.size() < EACH_NUM) {
            mListView.setHasMore(false);
            mListView.onBottomComplete();
        }
    }

    @Override
    public void onLoadMore() {
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mData.size() > 100) {
                    mListView.setHasMore(false);
                    mListView.onBottomComplete();
                    return;
                }
                addMoreData();
                mAdapter.notifyDataSetChanged();
                mListView.onBottomComplete();
            }
        }, 1500);
    }


}
