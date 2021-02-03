package com.zj.coherent;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

/**
 * Created by zhangjian on 2021/2/1.
 */
public class ProgressCoherentProcess {

    /**
     * 默认匀速动画最大的时长
     */
    private static final int MAX_UNIFORM_SPEED_DURATION = 8 * 1000;
    /**
     * 默认加速后减速动画最大时长
     */
    private static final int MAX_DECELERATE_SPEED_DURATION = 450;
    /**
     * 95f-100f时，透明度1f-0f时长
     */
    private static final int DO_END_ALPHA_DURATION = 630;
    /**
     * 95f - 100f动画时长
     */
    private static final int DO_END_PROGRESS_DURATION = 500;

    private static final int CONSTANT_SPEED_MAX_PROGRESS = 95;
    private static final int MAX_PROGRESS = 100;


    private Animator mAnimator;

    private View target;

    private ProgressUpdate progressUpdate;

    public ProgressCoherentProcess(View target, ProgressUpdate progressUpdate) {
        this.target = target;
        target.setVisibility(View.GONE);
        this.progressUpdate = progressUpdate;
    }

    private void startAnim(boolean toEnd) {

        if (mAnimator != null && mAnimator.isStarted()) {
            mAnimator.cancel();
        }
        mCurrentProgress = mCurrentProgress == 0f ? 0.00000001f : mCurrentProgress;

        if (mAnimator != null && mAnimator.isStarted()) {
            mAnimator.cancel();
        }
        mCurrentProgress = mCurrentProgress == 0f ? 0.00000001f : mCurrentProgress;

        if (toEnd) {
            //到100，
            Animator segmentStartAnimator = null;
            if (mCurrentProgress < CONSTANT_SPEED_MAX_PROGRESS) {
                segmentStartAnimator = segmentStartAnimator(MAX_DECELERATE_SPEED_DURATION);
//                segment95Animator.setInterpolator(new LinearInterpolator());
                segmentStartAnimator.setInterpolator(new DecelerateInterpolator());
            }

            ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(target, "alpha", 1f, 0f);
            alphaAnimator.setDuration(DO_END_ALPHA_DURATION);
            alphaAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    target.setVisibility(View.GONE);
                }
            });
            ValueAnimator segmentEndAnimator = ValueAnimator.ofFloat(CONSTANT_SPEED_MAX_PROGRESS, MAX_PROGRESS);
            segmentEndAnimator.setDuration(DO_END_PROGRESS_DURATION);
            segmentEndAnimator.addUpdateListener(mAnimatorUpdateListener);

            AnimatorSet mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(alphaAnimator, segmentEndAnimator);

            if (segmentStartAnimator != null) {
                AnimatorSet mAnimatorSet1 = new AnimatorSet();
                mAnimatorSet1.play(mAnimatorSet).after(segmentStartAnimator);
                mAnimatorSet = mAnimatorSet1;
            }
            mAnimatorSet.addListener(mAnimatorListenerAdapter);
            mAnimatorSet.start();
            mAnimator = mAnimatorSet;

            isEnd = true;
        } else {
            mAnimator = segmentStartAnimator(MAX_UNIFORM_SPEED_DURATION);
            mAnimator.setInterpolator(new LinearInterpolator());
            mAnimator.start();
        }
        inProgress = true;
    }

    private Animator segmentStartAnimator(int duration) {
        ValueAnimator constantSpeedAnimator = ValueAnimator.ofFloat(mCurrentProgress, CONSTANT_SPEED_MAX_PROGRESS);
        float residue = (CONSTANT_SPEED_MAX_PROGRESS - mCurrentProgress) / (float) MAX_PROGRESS;
        constantSpeedAnimator.setDuration((long) (residue * duration));
        constantSpeedAnimator.addUpdateListener(mAnimatorUpdateListener);
        return constantSpeedAnimator;
    }

    private float mCurrentProgress = -1;

    private boolean inProgress = false;
    private boolean isEnd = false;

    public void setProgress(float progress) {
        setProgress((int) (progress * 100));
    }

    public void setProgress(int newProgress) {
        if (newProgress <= 0) {
            reset();
        }
        if (newProgress >= 0) {
            if (newProgress < 100 && !inProgress) {
                target.setVisibility(View.VISIBLE);
                startAnim(false);
            }
            if (newProgress == 100 && !isEnd) {
                target.setVisibility(View.VISIBLE);
                startAnim(true);
            }
        } else {
            target.setVisibility(View.GONE);
        }
    }

    private ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            mCurrentProgress = (float) animation.getAnimatedValue();
            if (mCurrentProgress < CONSTANT_SPEED_MAX_PROGRESS) {
                target.setAlpha(1);
            }
            progressUpdate.update(mCurrentProgress);
        }
    };


    private AnimatorListenerAdapter mAnimatorListenerAdapter = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            reset();
        }
    };

    private void reset() {
        inProgress = false;
        isEnd = false;
        mCurrentProgress = -1;
    }

    public void release() {
        if (mAnimator != null && mAnimator.isStarted()) {
            mAnimator.cancel();
        }
    }

    public interface ProgressUpdate {
        void update(float progress);
    }
}

