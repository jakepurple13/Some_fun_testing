package com.example.myapplication;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.SeekBar;
import androidx.appcompat.widget.AppCompatSeekBar;

public class SmoothSeekBar extends AppCompatSeekBar implements SeekBar.OnSeekBarChangeListener {

    public interface SmoothSeekListener {
        void seeking(int value);
    }

    public SmoothSeekListener seekListener;

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener;

    private boolean mNeedCallListener = true;

    private ValueAnimator mAnimator;

    public SmoothSeekBar(Context context) {
        super(context);
        init(context);
    }

    public SmoothSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SmoothSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    public void init(Context context) {
        //Context mContext = context;
        context.getClass();
    }

    @Override
    public void setOnSeekBarChangeListener(
            SeekBar.OnSeekBarChangeListener onSeekBarChangeListener) {
        mOnSeekBarChangeListener = onSeekBarChangeListener;
        super.setOnSeekBarChangeListener(this);
    }

    @Override
    public void setProgress(final int progress) {
        final int currentProgress = getProgress();
        if (mAnimator != null) {
            mAnimator.cancel();
            mAnimator.removeAllUpdateListeners();
            mAnimator.removeAllListeners();
            mAnimator = null;
            mNeedCallListener = true;
        }
        mAnimator = ValueAnimator.ofInt(currentProgress, progress);
        mAnimator.setDuration(getResources().getInteger(android.R.integer.config_longAnimTime));
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                mNeedCallListener = value == progress;
                //Loged.INSTANCE.e(value + "", "ProgressBar value ", true);
                SmoothSeekBar.super.setProgress(value);
                if(seekListener!=null)
                    seekListener.seeking(value);
            }
        });
        mAnimator.start();

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser || mNeedCallListener) {
            if (mOnSeekBarChangeListener != null) {
                mOnSeekBarChangeListener.onProgressChanged(seekBar, progress, fromUser);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onStartTrackingTouch(seekBar);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onStopTrackingTouch(seekBar);
        }
    }
}
