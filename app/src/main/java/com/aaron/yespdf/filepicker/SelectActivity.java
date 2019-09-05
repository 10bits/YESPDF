package com.aaron.yespdf.filepicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aaron.base.impl.OnClickListenerImpl;
import com.aaron.yespdf.CommonActivity;
import com.aaron.yespdf.R;
import com.aaron.yespdf.R2;
import com.aaron.yespdf.UiManager;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PathUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SelectActivity extends CommonActivity implements View.OnClickListener, Communicable {

    @BindView(R2.id.app_iv_check) ImageView mCbSelectAll;
    @BindView(R2.id.app_horizontal_sv) HorizontalScrollView mHorizontalSv;
    @BindView(R2.id.app_ll) ViewGroup mVgPath;
    @BindView(R2.id.app_tv_path) TextView mTvPath;
    @BindView(R2.id.app_rv_select) RecyclerView mRvSelect;
    @BindView(R2.id.app_tv_import_count) TextView mTvImportCount;

    private Unbinder mUnbinder;
    private Listable mListable;
    private RecyclerView.Adapter mAdapter;
    private List<File> mFileList = new ArrayList<>();
    private List<String> mPathList = new ArrayList<>();

    private String mPreviousPath = "";
    private String mCurrentPath = PathUtils.getExternalStoragePath();

    private RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            mCbSelectAll.setSelected(false);
            mTvImportCount.setText(R.string.app_import_count);
            mPathList.clear();
            ((AdapterComm) mAdapter).init();
            handleCbSelectAll();
        }
    };

    public static void start(Context context) {
        Intent starter = new Intent(context, SelectActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int layoutId() {
        return R.layout.app_activity_select;
    }

    @Override
    protected Toolbar createToolbar() {
        return findViewById(R.id.app_toolbar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUnbinder = ButterKnife.bind(this);
        initToolbar();
        initView(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
        mAdapter.unregisterAdapterDataObserver(mDataObserver);
    }

    @Override
    public void onBackPressed() {
        if (mCurrentPath.endsWith("0")) {
            super.onBackPressed();
        } else {
            mPreviousPath = mCurrentPath.substring(0, mCurrentPath.lastIndexOf("/"));
            handleJump(mPreviousPath);
            backPath();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onClick(View view) {
        String path = (String) view.getTag();
        handleJump(path);
        jumpPath(view);
    }

    @Override
    public void onDirTap(String path) {
        LogUtils.e(path);
        handleJump(path);
        setCurPath(path);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSelectResult(List<String> pathList, int total) {
        mCbSelectAll.setSelected(pathList.size() == total);
        mTvImportCount.setText(getString(R.string.app_import) + "(" + pathList.size() + ")");
        mPathList.clear();
        mPathList.addAll(pathList);
    }

    private void jumpPath(View view) {

        int index = mVgPath.indexOfChild(view);
        int count = mVgPath.getChildCount();
        mVgPath.removeViews(index + 1,count - index - 1);
    }

    private void backPath() {
        int index = mVgPath.getChildCount() - 1;
        mVgPath.removeViews(index,1);
    }

    private void handleJump(String path) {
        mPreviousPath = mCurrentPath;
        mCurrentPath = path;
        mFileList.clear();
        mFileList.addAll(mListable.listFile(path));
        mAdapter.notifyDataSetChanged();
    }

    private void setCurPath(String path) {
        String curPath = path.substring(path.lastIndexOf("/") + 1);
        LayoutInflater inflater = LayoutInflater.from(this);
        TextView tvPath = (TextView) inflater.inflate(R.layout.app_include_tv_path, null);
        tvPath.setOnClickListener(this);
        tvPath.setText(curPath);
        tvPath.setTag(path);
        mVgPath.addView(tvPath);
    }

    private void initToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.app_ic_action_back_black);
        }
        mToolbar.setTitle(R.string.app_import_pdf);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.base_black));
        mToolbar.setContentInsetStartWithNavigation(0);
    }

    private void initView(Bundle savedInstanceState) {
        mTvPath.setOnClickListener(this);
        mTvPath.setTag(mCurrentPath);
        mTvImportCount.setOnClickListener(new OnClickListenerImpl() {
            @Override
            public void onViewClick(View v, long interval) {
                LogUtils.e(mPathList);
                if (mPathList.isEmpty()) {
                    UiManager.showShort(R.string.app_have_not_select);
                } else {
                    finish();
                    UiManager.showShort(R.string.app_import_success);
                }
            }
        });
        mCbSelectAll.setOnClickListener(v -> {
            mCbSelectAll.setSelected(!mCbSelectAll.isSelected());
            ((AdapterComm) mAdapter).selectAll();
        });

        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
        mRvSelect.setLayoutManager(lm);
        mListable = new ByNameListable();
        mFileList = mListable.listFile(PathUtils.getExternalStoragePath());
        mAdapter = new SelectAdapter(mFileList);
        mAdapter.registerAdapterDataObserver(mDataObserver);
        mRvSelect.setAdapter(mAdapter);
    }

    private void handleCbSelectAll() {
        boolean noFile = false;
        if (!mFileList.isEmpty()) {
            for (File file : mFileList) {
                noFile = !file.isFile();
            }
        } else {
            noFile = true;
        }
        mCbSelectAll.setEnabled(!noFile);
    }
}
