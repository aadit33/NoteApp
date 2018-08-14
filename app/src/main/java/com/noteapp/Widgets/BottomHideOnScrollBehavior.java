package com.noteapp.Widgets;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.noteapp.R;

public class BottomHideOnScrollBehavior extends CoordinatorLayout.Behavior<View> {
    private int distance;
    Context ctx;

    public BottomHideOnScrollBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctx = context;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {
        if (dy > 0 && distance < 0 || dy < 0 && distance > 0) {
            child.animate().cancel();
            distance = 0;
        }
        distance += dy;
        final int height = child.getHeight() > 0 ? (child.getHeight()) : 600/*update this accordingly*/;
        if (distance > height && child.isShown()) {
            hide(child);
        } else if (distance < 0 && !child.isShown()) {
            show(child);
        }
    }

    private void hide(View view) {
        Animation bottomUp = AnimationUtils.loadAnimation(ctx,
                R.anim.down);

        view.startAnimation(bottomUp);
        view.setVisibility(View.INVISIBLE);
    }

    private void show(View view) {
        Animation bottomUp = AnimationUtils.loadAnimation(ctx,
                R.anim.up);

        view.startAnimation(bottomUp);
        view.setVisibility(View.VISIBLE);// use animate.translateY(-height); instead
    }
}

