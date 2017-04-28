package it.carlom.stikkyheader.core;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

public abstract class StikkyHeader {

    protected Context mContext;

    protected View mHeader;
    protected int mMinHeightHeader;
    private HeaderAnimator mHeaderAnimator;
    protected int mHeightHeader;
    private View.OnTouchListener onTouchListenerOnHeaderDelegate;
    private boolean mAllowTouchBehindHeader;
    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener;
    
    protected StikkyHeader(Context context, View header, int minHeightHeader, HeaderAnimator headerAnimator) {
        mContext = context;
        mHeader = header;
        mMinHeightHeader = minHeightHeader;
        mHeaderAnimator = headerAnimator;
    }

    protected abstract View getScrollingView();

    /**
     * Init method called when the StikkyHeader is created
     */
    protected void init() {
        setupAnimator();
        measureHeaderHeight();
    }

    void build(boolean allowTouchBehindHeader) {
        mAllowTouchBehindHeader = allowTouchBehindHeader;
        setOnTouchListenerOnHeader(onTouchListenerOnHeaderDelegate);
        init();
    }

    private void measureHeaderHeight() {
        int height = mHeader.getHeight();

        if (height == 0) {
            ViewGroup.LayoutParams lp = mHeader.getLayoutParams();
            if (lp != null) {
                height = lp.height;
            }
        }

        if (height > 0) {
            setHeightHeader(height);
        }

        mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mHeightHeader != mHeader.getHeight()) {
                    setHeightHeader(mHeader.getHeight());
                }
            }
        };

        mHeader.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
    }

    protected void setHeightHeader(final int heightHeader) {
        mHeightHeader = heightHeader;
        mHeaderAnimator.setHeightHeader(heightHeader, mMinHeightHeader);
    }

    public void onScroll(int yScroll) {
        mHeaderAnimator.onScroll(yScroll);
    }

    private void setupAnimator() {
        mHeaderAnimator.setupAnimator(mHeader);
    }

    public void setMinHeightHeader(int minHeightHeader) {
        this.mMinHeightHeader = minHeightHeader;
        mHeaderAnimator.setHeightHeader(mHeightHeader, mMinHeightHeader);
    }

    public void setOnTouchListenerOnHeader(View.OnTouchListener onTouchListenerOnHeaderDelegate) {
        this.onTouchListenerOnHeaderDelegate = onTouchListenerOnHeaderDelegate;
        StikkyHeaderUtils.setOnTouchListenerOnHeader(mHeader, getScrollingView(), onTouchListenerOnHeaderDelegate, mAllowTouchBehindHeader);
    }

    public View getHeader() {
        return mHeader;
    }

    public void cleanMemory() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mHeader.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
        }
        else {
            mHeader.getViewTreeObserver().removeGlobalOnLayoutListener(mOnGlobalLayoutListener);
        }
    }
}
