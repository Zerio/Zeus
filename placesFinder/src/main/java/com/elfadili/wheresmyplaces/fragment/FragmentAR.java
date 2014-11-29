package com.elfadili.wheresmyplaces.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elfadili.wheresmyplaces.constants.IMarocPlaceExplorerConstants;
import com.places.nearby.R;
import com.elfadili.wheresmyplaces.MainActivity;

public class FragmentAR  extends Fragment implements IMarocPlaceExplorerConstants {
    public static final String TAG = FragmentAR.class.getSimpleName();

    private View mRootView;

    private MainActivity mContext;

    private boolean isFindView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_ar, container, false);
        return mRootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!isFindView){
            isFindView=true;
            this.findView();
        }
    }

    private void findView(){
        this.mContext = (MainActivity) getActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
