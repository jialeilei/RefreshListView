package com.example.lei.refreshlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by lei on 2016/11/18.
 */
public class RefreshListView extends ListView implements AbsListView.OnScrollListener{
    private static final String TAG = "RefreshListView";
    private View mHeader;
    private int mHeaderHeight;
    /*
    * 第一个可见视图的位置
    * */
    private int mFirstViewItem;
    /*
    *滑动的状态
    * */
    private int scrollState;
    /*
    *是否在listView 最顶端按下
    * */
    private boolean isRemark = true;
    private int startY;
    /*
    * 当前状态
    * */
    private int state;
    final int NONE = 0;//正常状态
    final int PULL = 1;//下拉状态
    final int RELESE = 2;//放开状态
    final int REFLASHING = 3;//刷新状态
    private TextView tvShow;
    private ProgressBar progressBar;
    private ImageView img;

    public RefreshListView(Context context) {
        super(context);
        initView(context);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 添加顶部布局文件
     * @param context
     */
    private void initView(Context context){
        mHeader = LayoutInflater.from(context).inflate(R.layout.listview_header,null);
        tvShow = (TextView) mHeader.findViewById(R.id.tvShow);
        progressBar = (ProgressBar) mHeader.findViewById(R.id.progressBar);
        img = (ImageView) mHeader.findViewById(R.id.img);
        measureView(mHeader);
        mHeaderHeight = mHeader.getMeasuredHeight();
        Log.i(TAG,"height: "+ mHeaderHeight);
        topPadding(-mHeaderHeight);
        this.addHeaderView(mHeader);
        this.setOnScrollListener(this);

    }

    /**
     * 通知父布局占用宽、高
     */
    private void measureView(View view) {
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p == null){
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        }
        int width = ViewGroup.getChildMeasureSpec(0,0,p.width);
        int height;
        int tempHeight = p.height;
        if (tempHeight > 0){
            height = MeasureSpec.makeMeasureSpec(tempHeight,MeasureSpec.EXACTLY);
        }else {
            height = MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED);
        }
        view.measure(width, height);
    }


    private void topPadding(int topPadding){
        /*
        * 限制下拉的最高长度为  mHeaderHeight + 100
        * */
        //topPadding = topPadding > mHeaderHeight ? mHeaderHeight : topPadding;
        mHeader.setPadding(mHeader.getPaddingLeft(),topPadding,mHeader.getPaddingRight(),mHeader.getPaddingBottom());
        mHeader.invalidate();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        this.scrollState = scrollState;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        mFirstViewItem = firstVisibleItem;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (mFirstViewItem == 0){
                    startY = (int)ev.getY();
                    isRemark = true;
                }
                Log.i(TAG, "down startY: " + startY);
                break;
            case MotionEvent.ACTION_MOVE:

                onMove(ev);

                break;
            case MotionEvent.ACTION_UP:

                if (state == RELESE){
                    state = REFLASHING;
                    /*
                    * 更新数据
                    * */
                    if (mOnRefreshListener != null){
                        mOnRefreshListener.refresh();
                    }

                }/*else if (state == PULL){
                    state = NONE;
                }*/
                else {
                    state = NONE;
                }

                refreshViewByState();

                break;
            default:
                break;
        }

        return super.onTouchEvent(ev);
    }

    private void onMove(MotionEvent ev) {
        if (!isRemark){
            return;
        }
        int tempY = (int) ev.getY();
        int space = tempY - startY;
        int topPadding = space - mHeaderHeight;
        switch (state){
            case NONE:
                if (space > 10){
                    state = PULL;
                    refreshViewByState();
                }

                break;
            case PULL:
                topPadding(topPadding);
                if (space > mHeaderHeight && scrollState == SCROLL_STATE_TOUCH_SCROLL){
                    state = RELESE;
                    refreshViewByState();
                }

                break;
            case RELESE:
                topPadding(topPadding);
                if (space > 0 && space < mHeaderHeight){
                    state = PULL;
                }else if (space <= 0){
                    state = NONE;
                    isRemark = false;
                }
                refreshViewByState();

                break;
            case REFLASHING:

                refreshViewByState();
                Log.i(TAG, "refresh");

                break;
            default:

                break;
        }
    }

    /**
     * 根据状态更新 Header
     */
    private void refreshViewByState(){

        RotateAnimation animTurn = new RotateAnimation(0,180,RotateAnimation.RELATIVE_TO_SELF,0.5f,RotateAnimation.RELATIVE_TO_SELF,0.5f);
        animTurn.setDuration(500);
        animTurn.setFillAfter(true);

        RotateAnimation animBack = new RotateAnimation(180,0,RotateAnimation.RELATIVE_TO_SELF,0.5f,RotateAnimation.RELATIVE_TO_SELF,0.5f);
        animBack.setDuration(500);
        animBack.setFillAfter(true);

        switch (state){
            case NONE:
                topPadding(-mHeaderHeight);

                break;
            case PULL:
                tvShow.setText("下拉刷新");

                img.setVisibility(VISIBLE);
                progressBar.setVisibility(GONE);
                img.clearAnimation();
                img.setAnimation(animTurn);

                break;
            case RELESE:
                tvShow.setText("松开刷新");
                img.setVisibility(VISIBLE);
                progressBar.setVisibility(GONE);
                img.clearAnimation();
                img.setAnimation(animBack);

                break;
            case REFLASHING:
                topPadding(mHeaderHeight);
                tvShow.setText("正在刷新");
                img.setVisibility(GONE);
                img.clearAnimation();
                progressBar.setVisibility(VISIBLE);

                break;
            default:

                break;
        }
    }

    private OnRefreshListener mOnRefreshListener = null;


    /**
     * 停止刷新数据
     */
    public void refreshComplete() {
        state = NONE;
        refreshViewByState();
    }

    /**
     * 刷新接口
     */
    public interface OnRefreshListener{
        void refresh();
    }

    public void setOnRefreshListener(OnRefreshListener listener){
        this.mOnRefreshListener = listener;
    }

}
