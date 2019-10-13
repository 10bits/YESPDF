package com.aaron.yespdf.filepicker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aaron.base.base.BaseFragment;
import com.aaron.base.impl.OnClickListenerImpl;
import com.aaron.yespdf.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AutoScanFragment extends BaseFragment {

    private static final int REQUEST_CODE = 1;

    @BindView(R.id.app_btn_scan)
    Button btnScan;

    private Unbinder unbinder;
    private SelectActivity activity;

    static Fragment newInstance() {
        return new AutoScanFragment();
    }

    public AutoScanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = (SelectActivity) getActivity();
        View view = inflater.inflate(R.layout.app_fragment_auto_scan, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.ibtnSelectAll.setVisibility(View.GONE);
        activity.ibtnSearch.setVisibility(View.GONE);
        activity.closeSearchView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            activity.finish();
        }
    }

    private void initView() {
        btnScan.setOnClickListener(new OnClickListenerImpl() {
            @Override
            public void onViewClick(View v, long interval) {
                ScanActivity.start(activity, (ArrayList<String>) activity.importeds, SelectActivity.REQUEST_CODE);
            }
        });
    }
}