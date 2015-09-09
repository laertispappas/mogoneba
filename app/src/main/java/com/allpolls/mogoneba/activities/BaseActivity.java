package com.allpolls.mogoneba.activities;

import android.animation.Animator;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;

import com.allpolls.mogoneba.R;
import com.allpolls.mogoneba.infrastructure.ActionScheduler;
import com.allpolls.mogoneba.infrastructure.MogonebaApplication;
import com.allpolls.mogoneba.views.NavDrawer;
import com.squareup.otto.Bus;

public abstract class BaseActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {
    private boolean isRegisteredWithBus;

    protected MogonebaApplication application;
    protected Toolbar toolbar;
    protected NavDrawer navDrawer;
    protected boolean isTablet;
    protected Bus bus;
    protected ActionScheduler scheduler;
    protected SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedState){
        super.onCreate(savedState);
        application = (MogonebaApplication) getApplication();
        bus = application.getBus();
        scheduler = new ActionScheduler(application);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        isTablet = (metrics.widthPixels / metrics.density) >= 600;

        bus.register(this);
        isRegisteredWithBus = true;
    }

    public ActionScheduler getScheduler(){
        return scheduler;
    }

    @Override
    protected void onResume(){
        super.onResume();
        scheduler.onResume();
    }

    @Override protected void onPause(){
        super.onPause();
        scheduler.onPause();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if(isRegisteredWithBus) {
            bus.unregister(this);
            isRegisteredWithBus = false;
        }

        // not all activities have a navDrawer
        if(navDrawer != null)
            navDrawer.destroy();
    }

    @Override
    public void finish(){
        super.finish();

        if(isRegisteredWithBus) {
            bus.unregister(this);
            isRegisteredWithBus = false;
        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutResId){
        super.setContentView(layoutResId);

        toolbar = (Toolbar) findViewById(R.id.include_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(this);
            swipeRefresh.setColorSchemeColors(
                    Color.parseColor("#FF00DDFF"),
                    Color.parseColor("#FF99CC00"),
                    Color.parseColor("#FFFFBB33"),
                    Color.parseColor("#FFFF4444")
            );
        }
    }

    public void fadeOut(final FadeOutListener listener) {
        View rootView = findViewById(android.R.id.content);
        rootView.animate()
                    .alpha(0)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            listener.onFadeOutEnd();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    })
                    .setDuration(300)
                    .start();
    }

    protected void setNavDrawer(NavDrawer drawer){
        this.navDrawer = drawer;
        this.navDrawer.create();

        overridePendingTransition(0, 0);

        View rootView = findViewById(android.R.id.content);
        rootView.setAlpha(0);
        rootView.animate().alpha(1).setDuration(150).start();
    }

    public Toolbar getToolbar(){
        return this.toolbar;
    }

    public MogonebaApplication getMogonebaApplication() {
        return application;
    }

    @Override
    public void onRefresh() {

    }

    public interface FadeOutListener {
        void onFadeOutEnd();
    }
}
