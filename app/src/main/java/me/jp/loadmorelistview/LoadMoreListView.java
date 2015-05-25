package me.jp.loadmorelistview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

/**
 * load more listView
 * Created by JiangPing on 2015/5/25.
 */
public class LoadMoreListView extends ListView implements AbsListView.OnScrollListener {

    private boolean isOnBottomStyle = true;
    /**
     * footer layout view *
     */
    private RelativeLayout footerLayout;
    private ProgressBar footerProgressBar;
    private Button footerButton;

    private OnScrollListener onScrollListener;

    /**
     * whether bottom listener has more *
     */
    private boolean hasMore = true;

    /**
     * whether show footer loading progress bar when loading *
     */
    private boolean isShowFooterProgressBar = true;

    /**
     * whether is on bottom loading *
     */
    private boolean isOnBottomLoading = false;

    private String footerDefaultText;
    private String footerLoadingText;
    private String footerNoMoreText;

    private Context mContext;


    public LoadMoreListView(Context context) {
        this(context, null);
    }

    public LoadMoreListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs,
                R.styleable.SwipeListView);
        isOnBottomStyle = ta.getBoolean(R.styleable.SwipeListView_swipeIsOnBottomStyle,
                true);
        ta.recycle();
        initOnBottomStyle();
        super.setOnScrollListener(this);
    }

    /**
     * init on bottom style, only init once
     */
    private void initOnBottomStyle() {
        if (footerLayout != null) {
            if (isOnBottomStyle) {
                addFooterView(footerLayout);
            } else {
                removeFooterView(footerLayout);
            }
            return;
        }
        if (!isOnBottomStyle) {
            return;
        }

        footerDefaultText = mContext.getString(R.string.load_more_list_footer_default_text);
        footerLoadingText = mContext.getString(R.string.load_more_list_footer_loading_text);
        footerNoMoreText = mContext.getString(R.string.load_more_list_footer_no_more_text);

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerLayout = (RelativeLayout) inflater.inflate(R.layout.load_more_list_footer, this,
                false);
        footerButton = (Button) footerLayout.findViewById(R.id.btn_footer);
        footerButton.setDrawingCacheBackgroundColor(0);
        footerButton.setEnabled(true);

        footerProgressBar = (ProgressBar) footerLayout
                .findViewById(R.id.progress_bar_footer);
        addFooterView(footerLayout);
    }


    private OnLoadMoreListener onLoadMoreListener;

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        onLoadMoreListener = listener;
        if (null != footerButton) {
            footerButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLoadMoreListener.onLoadMore();
                }
            });
        }
    }

    /**
     * @return isOnBottomStyle
     */
    public boolean isOnBottomStyle() {
        return isOnBottomStyle;
    }

    /**
     * @param isOnBottomStyle
     */
    public void setOnBottomStyle(boolean isOnBottomStyle) {
        if (this.isOnBottomStyle != isOnBottomStyle) {
            this.isOnBottomStyle = isOnBottomStyle;
            initOnBottomStyle();
        }
    }

    /**
     * get whether show footer loading progress bar when loading
     *
     * @return the isShowFooterProgressBar
     */
    public boolean isShowFooterProgressBar() {
        return isShowFooterProgressBar;
    }

    /**
     * set whether show footer loading progress bar when loading
     *
     * @param isShowFooterProgressBar
     */
    public void setShowFooterProgressBar(boolean isShowFooterProgressBar) {
        this.isShowFooterProgressBar = isShowFooterProgressBar;
    }

    /**
     * get footer button
     *
     * @return
     */
    public Button getFooterButton() {
        return footerButton;
    }

    /**
     * set whether has more. if hasMore is false, onBottom will not be called
     * when listView scroll to bottom
     *
     * @param hasMore
     */
    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    /**
     * get whether has more
     *
     * @return
     */
    public boolean isHasMore() {
        return hasMore;
    }

    @Override
    public void setOnScrollListener(OnScrollListener listener) {
        onScrollListener = listener;
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (onScrollListener != null) {
            onScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        // if isOnBottomStyle and  hasMore, then call
        // onBottom function auto
        if (isOnBottomStyle && hasMore) {
            if (firstVisibleItem > 0 && totalItemCount > 0
                    && (firstVisibleItem + visibleItemCount == totalItemCount)) {
                onBottom();
            }
        }
        if (onScrollListener != null) {
            onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    /**
     * on bottom begin, adjust view status
     */
    private void onBottomBegin() {
        if (isOnBottomStyle) {
            if (isShowFooterProgressBar) {
                Log.i("TestData","isShowFooterProgressBar:" + isShowFooterProgressBar);
                footerProgressBar.setVisibility(View.VISIBLE);
                Log.i("TestData", "footerProgressBar>>>>>getVisibility:" + (footerProgressBar.getVisibility()==View.VISIBLE));
            }
            footerButton.setText(footerLoadingText);
            footerButton.setEnabled(false);
        }
    }

    /**
     * on bottom loading, you can call it by manual, but you should manual call
     * onBottomComplete at the same time.
     */
    public void onBottom() {
        if (isOnBottomStyle && !isOnBottomLoading) {
            isOnBottomLoading = true;
            onBottomBegin();
            footerButton.performClick();
        }
    }

    /**
     * on bottom load complete, restore view status
     */
    public void onBottomComplete() {
        if (isOnBottomStyle) {
            if (isShowFooterProgressBar) {
                footerProgressBar.setVisibility(View.GONE);
            }
            footerButton.setEnabled(true);
            if (!hasMore) {
                footerButton.setText(footerNoMoreText);
            } else {
                footerButton.setText(footerDefaultText);
            }
            isOnBottomLoading = false;
        }
    }

}
