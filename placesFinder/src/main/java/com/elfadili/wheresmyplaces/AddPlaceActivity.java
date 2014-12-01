package com.elfadili.wheresmyplaces;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.andreabaccega.widget.FormEditText;
import com.dd.processbutton.iml.ActionProcessButton;
import com.elfadili.wheresmyplaces.dataMng.TotalDataManager;
import com.parse.ParseObject;
import com.places.nearby.R;

public class AddPlaceActivity extends Activity {

    private FormEditText mEtPlaceName;
    private FormEditText mEtPlaceAddress;
    private FormEditText mEtPlacePhone;
    private FormEditText mEtPlaceWeb;
    private FormEditText mEtPlaceEmail;
    private ImageView mImgCat;

    ActionProcessButton btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        Intent intent = getIntent();
        final String category = intent.getStringExtra("Category");

        ActionBar ab = getActionBar();
        ab.setTitle("Ajouter un(e) " + category.toLowerCase());
        //setTitle("Ajouter un(e) " + category.toLowerCase());

        mEtPlaceName = (FormEditText) findViewById(R.id.plc_name);
        mEtPlaceAddress = (FormEditText) findViewById(R.id.plc_address);
        mEtPlacePhone = (FormEditText) findViewById(R.id.plc_phone);
        mEtPlaceWeb = (FormEditText) findViewById(R.id.plc_web);
        mEtPlaceEmail = (FormEditText) findViewById(R.id.plc_email);
        mImgCat = (ImageView) findViewById(R.id.img_category);

        mImgCat.setImageResource(TotalDataManager.getInstance().getResIconHome(this, category));
        btnSend = (ActionProcessButton) findViewById(R.id.btnSend);

        btnSend.setMode(ActionProcessButton.Mode.ENDLESS);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSend.setProgress(1);

                FormEditText[] allFields = {
                        mEtPlaceName,
                        mEtPlaceAddress,
                        mEtPlaceWeb,
                        mEtPlacePhone,
                        mEtPlaceEmail };

                boolean allValid = true;
                for (FormEditText field: allFields) {
                    allValid = field.testValidity() && allValid;
                }

                if (allValid) {
                    ParseObject placeObject = new ParseObject("Place");
                    placeObject.put("Category", category);
                    placeObject.put("Name", mEtPlaceName.getText().toString());
                    placeObject.put("Address", mEtPlaceAddress.getText().toString());
                    placeObject.put("WebSite", mEtPlaceWeb.getText().toString());
                    placeObject.put("PhoneNumber", mEtPlacePhone.getText().toString());
                    placeObject.put("Email", mEtPlaceEmail.getText().toString());
                    placeObject.saveInBackground();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            btnSend.setProgress(100);
                            // Clear all
                            RelativeLayout group = (RelativeLayout)findViewById(R.id.AddPlace);
                            for (int i = 0, count = group.getChildCount(); i < count; ++i)
                            {
                                View v = group.getChildAt(i);
                                if (v instanceof EditText) {
                                    ((FormEditText)v).setText("");
                                }
                            }
                        }
                    }, 3000);
                } else {
                    btnSend.setProgress(-1);
                    //Toast.makeText(AddPlaceActivity.this, getString(R.string.msg_error), Toast.LENGTH_LONG).show();
                }
            }
        });

        mEtPlaceName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                btnSend.setProgress(0);
                return false;
            }
        });
        mEtPlaceAddress.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                btnSend.setProgress(0);
                return false;
            }
        });
        mEtPlaceWeb.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                btnSend.setProgress(0);
                return false;
            }
        });
        mEtPlacePhone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                btnSend.setProgress(0);
                return false;
            }
        });
        mEtPlaceEmail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                btnSend.setProgress(0);
                return false;
            }
        });
    }
}
