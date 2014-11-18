package com.elfadili.wheresmyplaces;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

//import com.ToxicBakery.viewpager.transforms.ZoomOutTranformer;
import com.elfadili.wheresmyplaces.adapter.WelcomeAdapter;
import com.places.nearby.R;

public class WelcomeActivity extends FragmentActivity {

    public static final String TAG=WelcomeActivity.class.getSimpleName();
    private ViewPager steps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        setContentView(R.layout.activity_welcome);

        steps = (ViewPager) findViewById(R.id.welcome);
        WelcomeAdapter stepsAdapter = new WelcomeAdapter(getSupportFragmentManager());
        steps.setAdapter(stepsAdapter);
        //steps.setPageTransformer(true, new ZoomOutTranformer());
    }
}