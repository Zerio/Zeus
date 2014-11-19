package com.elfadili.wheresmyplaces.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.elfadili.wheresmyplaces.MainActivity;
import com.elfadili.wheresmyplaces.constanst.IMarocPlaceExplorerConstants;
import com.places.nearby.R;

public class FragmentStep4 extends Fragment implements IMarocPlaceExplorerConstants {

    public FragmentStep4() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_step4, container, false);

        Button newPage = (Button) view.findViewById(R.id.start);
        newPage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getActivity(), MainActivity.class);
                mIntent.putExtra(KEY_START_FROM, START_FROM_WELCOME);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
            }
        });
        return view;
    }
}
