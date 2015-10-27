/*
 * Copyright (C) 2015 pengjianbo(pengjianbosoft@gmail.com), Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package cn.finalteam.floatviewfinal.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.finalteam.floatviewfinal.utils.ResourceUtils;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Desction:悬浮窗
 * Author:pengjianbo
 * Date:15/10/26 下午8:39
 */
public class FloatView extends FrameLayout implements OnTouchListener {

    private final int HANDLER_TYPE_TIMER = 100;

    private WindowManager.LayoutParams mWmParams;
    private WindowManager mWindowManager;
    private Context mContext;

    private View mRootFloatView;
    private ImageView mIvFloatLogo;
    private ImageView mIvFloatLoader;
    private LinearLayout mLlFloatMenu;
    private TextView mTvAccount;
    private TextView mTvFeedback;
    private FrameLayout mFlFloatLogo;

    private boolean mIsRight;//logo是否在右边
    private boolean mCanHide;//是否允许隐藏
    private float mTouchStartX;
    private float mTouchStartY;
    private int mScreenWidth;
    private int mScreenHeight;
    private boolean mDraging;

    private Timer mTimer;
    private TimerTask mTimerTask;

    final Handler mTimerHandler = new Handler() {
        public void handleMessage(Message msg) {
            if ( msg.what == HANDLER_TYPE_TIMER ) {
                // 比如隐藏悬浮框
                if (mCanHide) {
                    mCanHide = false;
                    if (mIsRight) {
                        mIvFloatLogo.setImageResource(ResourceUtils.getDrawableId(
                                mContext, "pj_image_float_right"));
                        mIvFloatLoader.clearAnimation();
                        mIvFloatLoader.setVisibility(View.GONE);
                    } else {
                        mIvFloatLogo.setImageResource(ResourceUtils.getDrawableId(
                                mContext, "pj_image_float_left"));
                        mIvFloatLoader.clearAnimation();
                        mIvFloatLoader.setVisibility(View.GONE);
                    }
                    mWmParams.alpha = 0.7f;
                    mWindowManager.updateViewLayout(FloatView.this, mWmParams);
                    refreshFloatMenu(mIsRight);
                    mLlFloatMenu.setVisibility(View.GONE);
                }
            }
            super.handleMessage(msg);
        }
    };

    public FloatView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context mContext) {
        this.mContext = mContext;

        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        // 更新浮动窗口位置参数 靠边
        DisplayMetrics dm = new DisplayMetrics();
        // 获取屏幕信息
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        this.mWmParams = new WindowManager.LayoutParams();
        // 设置window type
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            mWmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        // 设置图片格式，效果为背景透明
        mWmParams.format = PixelFormat.RGBA_8888;
        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        mWmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 调整悬浮窗显示的停靠位置为左侧置�?
        mWmParams.gravity = Gravity.LEFT | Gravity.TOP;

        mScreenHeight = mWindowManager.getDefaultDisplay().getHeight();

        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        mWmParams.x = 0;
        mWmParams.y = mScreenHeight / 2;

        // 设置悬浮窗口长宽数据
        mWmParams.width = LayoutParams.WRAP_CONTENT;
        mWmParams.height = LayoutParams.WRAP_CONTENT;
        addView(createView(mContext));
        mWindowManager.addView(this, mWmParams);

        hide();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // 更新浮动窗口位置参数 靠边
        DisplayMetrics dm = new DisplayMetrics();
        // 获取屏幕信息
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        int oldX = mWmParams.x;
        int oldY = mWmParams.y;
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE://横屏
                if ( mIsRight ) {
                    mWmParams.x = mScreenWidth;
                    mWmParams.y = oldY;
                } else {
                    mWmParams.x = oldX;
                    mWmParams.y = oldY;
                }
                break;
            case Configuration.ORIENTATION_PORTRAIT://竖屏
                if ( mIsRight ) {
                    mWmParams.x = mScreenWidth;
                    mWmParams.y = oldY;
                } else {
                    mWmParams.x = oldX;
                    mWmParams.y = oldY;
                }
                break;
        }
        mWindowManager.updateViewLayout(this, mWmParams);
    }

    /**
     * 创建Float view
     * @param context
     * @return
     */
    private View createView(final Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        // 从布局文件获取浮动窗口视图
        mRootFloatView = inflater.inflate(ResourceUtils.getLayoutId(context, "pj_widget_float_view"), null);
        mFlFloatLogo = (FrameLayout) mRootFloatView.findViewById(ResourceUtils.getId(context, "pj_float_view"));

        mIvFloatLogo = (ImageView) mRootFloatView.findViewById(ResourceUtils.getId(context,
                "pj_float_view_icon_imageView"));
        mIvFloatLoader = (ImageView) mRootFloatView.findViewById(ResourceUtils.getId(
                context, "pj_float_view_icon_notify"));
        mLlFloatMenu = (LinearLayout) mRootFloatView.findViewById(ResourceUtils.getId(
                context, "ll_menu"));
        mTvAccount = (TextView) mRootFloatView.findViewById(ResourceUtils.getId(
                context, "tv_account"));
        mTvAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mLlFloatMenu.setVisibility(View.GONE);
                openUserCenter();
            }
        });
        mTvFeedback = (TextView) mRootFloatView.findViewById(ResourceUtils.getId(
                context, "tv_feedback"));
        mTvFeedback.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                openFeedback();
                mLlFloatMenu.setVisibility(View.GONE);
            }
        });
        mRootFloatView.setOnTouchListener(this);
        mRootFloatView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( !mDraging ) {
                    if (mLlFloatMenu.getVisibility() == View.VISIBLE) {
                        mLlFloatMenu.setVisibility(View.GONE);
                    } else {
                        mLlFloatMenu.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        mRootFloatView.measure(MeasureSpec.makeMeasureSpec(0,
                MeasureSpec.UNSPECIFIED), MeasureSpec
                .makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));


        return mRootFloatView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        removeTimerTask();
        // 获取相对屏幕的坐标，即以屏幕左上角为原点
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartX = event.getX();
                mTouchStartY = event.getY();
                mIvFloatLogo.setImageResource(ResourceUtils.getDrawableId(
                        mContext, "pj_image_float_logo"));
                mWmParams.alpha = 1f;
                mWindowManager.updateViewLayout(FloatView.this, mWmParams);
                break;
            case MotionEvent.ACTION_MOVE:
                float mMoveStartX = event.getX();
                float mMoveStartY = event.getY();
                // 如果移动量大于3才移动
                if (Math.abs(mTouchStartX - mMoveStartX) > 3
                        && Math.abs(mTouchStartY - mMoveStartY) > 3) {
                    // 更新浮动窗口位置参数
                    mWmParams.x = (int) (x - mTouchStartX);
                    mWmParams.y = (int) (y - mTouchStartY);
                    mWindowManager.updateViewLayout(this, mWmParams);
                    mLlFloatMenu.setVisibility(View.GONE);
                    return false;
                }
                mDraging = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                if (mWmParams.x >= mScreenWidth / 2) {
                    mWmParams.x = mScreenWidth;
                    mIsRight = true;
                } else if (mWmParams.x < mScreenWidth / 2) {
                    mIsRight = false;
                    mWmParams.x = 0;
                }
                mIvFloatLogo.setImageResource(ResourceUtils.getDrawableId(
                        mContext, "pj_image_float_logo"));
                refreshFloatMenu(mIsRight);
                timerForHide();
                mWindowManager.updateViewLayout(this, mWmParams);
                // 初始化
                mTouchStartX = mTouchStartY = 0;

                mDraging = false;
                break;
        }
        return false;
    }

    private void removeTimerTask() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    private void removeFloatView() {
        try {
            if (mRootFloatView != null) {
                mWindowManager.removeView(mRootFloatView);
            }
        } catch (Exception ex) {
        }
    }

    /**
     * 隐藏悬浮窗
     */
    public void hide() {
        if (mRootFloatView != null) {
            mRootFloatView.setVisibility(View.GONE);
            removeTimerTask();
        }
    }

    /**
     * 显示悬浮窗
     */
    public void show() {
        if (mRootFloatView != null) {
            mIvFloatLogo.setImageResource(ResourceUtils.getDrawableId(
                    mContext, "pj_image_float_logo"));
            mWmParams.alpha = 1f;
            mWindowManager.updateViewLayout(FloatView.this, mWmParams);
            mRootFloatView.setVisibility(View.VISIBLE);
            timerForHide();
            if (mIvFloatLoader.getVisibility() == View.VISIBLE) {
                Animation rotaAnimation = AnimationUtils.loadAnimation(mContext,
                        ResourceUtils.getAnimId(mContext, "pj_loading_anim"));
                rotaAnimation.setInterpolator(new LinearInterpolator());
                mIvFloatLoader.startAnimation(rotaAnimation);
            }
        }
    }

    /**
     * 刷新float view menu
     * @param right
     */
    private void refreshFloatMenu(boolean right) {
        if (right) {
            LayoutParams paramsFloatImage = (LayoutParams) mIvFloatLogo.getLayoutParams();
            paramsFloatImage.gravity = Gravity.RIGHT;
            mIvFloatLogo.setLayoutParams(paramsFloatImage);
            LayoutParams paramsFlFloat = (LayoutParams) mFlFloatLogo.getLayoutParams();
            paramsFlFloat.gravity = Gravity.RIGHT;
            mFlFloatLogo.setLayoutParams(paramsFlFloat);

            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, mContext.getResources().getDisplayMetrics());
            int padding52 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 52, mContext.getResources().getDisplayMetrics());
            LinearLayout.LayoutParams paramsMenuAccount = (LinearLayout.LayoutParams) mTvAccount.getLayoutParams();
            paramsMenuAccount.rightMargin = padding;
            paramsMenuAccount.leftMargin = padding;
            mTvAccount.setLayoutParams(paramsMenuAccount);

            LinearLayout.LayoutParams paramsMenuFb = (LinearLayout.LayoutParams) mTvFeedback.getLayoutParams();
            paramsMenuFb.rightMargin = padding52;
            paramsMenuFb.leftMargin = padding;
            mTvFeedback.setLayoutParams(paramsMenuFb);
        } else {
            LayoutParams params = (LayoutParams) mIvFloatLogo.getLayoutParams();
            params.setMargins(0, 0, 0, 0);
            params.gravity = Gravity.LEFT;
            mIvFloatLogo.setLayoutParams(params);
            LayoutParams paramsFlFloat = (LayoutParams) mFlFloatLogo.getLayoutParams();
            paramsFlFloat.gravity = Gravity.LEFT;
            mFlFloatLogo.setLayoutParams(paramsFlFloat);

            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, mContext.getResources().getDisplayMetrics());
            int padding52 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 52, mContext.getResources().getDisplayMetrics());

            LinearLayout.LayoutParams paramsMenuAccount = (LinearLayout.LayoutParams) mTvAccount.getLayoutParams();
            paramsMenuAccount.rightMargin = padding;
            paramsMenuAccount.leftMargin = padding52;
            mTvAccount.setLayoutParams(paramsMenuAccount);

            LinearLayout.LayoutParams paramsMenuFb = (LinearLayout.LayoutParams) mTvFeedback.getLayoutParams();
            paramsMenuFb.rightMargin = padding;
            paramsMenuFb.leftMargin = padding;
            mTvFeedback.setLayoutParams(paramsMenuFb);
        }
    }

    /**
     * 定时隐藏float view
     */
    private void timerForHide() {
        mCanHide = true;
        mTimer = new Timer();

        //结束任务
        if (mTimerTask != null) {
            try {
                mTimerTask.cancel();
                mTimerTask = null;
            } catch (Exception e){}

        }
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = mTimerHandler.obtainMessage();
                message.what = HANDLER_TYPE_TIMER;
                mTimerHandler.sendMessage(message);
            }
        };
        if (mCanHide) {
            mTimer.schedule(mTimerTask, 6000, 3000);
        }
    }

    /**
     * 打开用户中心
     */
    private void openUserCenter() {
    }

    /**
     * 打开客服页面
     */
    private void openFeedback() {
    }

    /**
     * 是否Float view
     */
    public void destroy() {
        hide();
        removeFloatView();
        removeTimerTask();
        try {
            mTimerHandler.removeMessages(1);
        } catch (Exception e){}
    }
}
