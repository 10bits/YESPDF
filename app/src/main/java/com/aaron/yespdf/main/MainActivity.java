package com.aaron.yespdf.main;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.aaron.base.impl.OnClickListenerImpl;
import com.aaron.yespdf.R;
import com.aaron.yespdf.R2;
import com.aaron.yespdf.about.AboutActivity;
import com.aaron.yespdf.common.CommonActivity;
import com.aaron.yespdf.common.DataManager;
import com.aaron.yespdf.common.DialogManager;
import com.aaron.yespdf.common.UiManager;
import com.aaron.yespdf.common.event.HotfixEvent;
import com.aaron.yespdf.common.event.ImportEvent;
import com.aaron.yespdf.common.widgets.NewViewPager;
import com.aaron.yespdf.filepicker.SelectActivity;
import com.aaron.yespdf.settings.SettingsActivity;
import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author Aaron aaronzzxup@gmail.com
 */
public class MainActivity extends CommonActivity implements IMainContract.V {

    private static final int SELECT_REQUEST_CODE = 101;

    @BindView(R2.id.app_vg_operation)
    ViewGroup vgOperationBar;
    @BindView(R2.id.app_ibtn_cancel)
    ImageButton ibtnCancel;
    @BindView(R2.id.app_tv_title)
    TextView tvTitle;
    @BindView(R2.id.app_ibtn_delete)
    ImageButton ibtnDelete;
    @BindView(R2.id.app_ibtn_select_all)
    ImageButton ibtnSelectAll;

    @BindView(R2.id.app_tab_layout)
    TabLayout tabLayout;
    @BindView(R2.id.app_vp)
    NewViewPager vp;

    private IMainContract.P presenter;
    private IOperation operation;

    private Unbinder unbinder;
    private Dialog loadingDialog;
    private Dialog hotfixDialog;
    private TextView tvDeleteDescription;
    private BottomSheetDialog deleteDialog;
    private PopupWindow pwMenu;
    private FragmentPagerAdapter fragmentPagerAdapter;

    // 导入回调
    private BottomSheetDialog importInfoDialog;
    private TextView tvDialogTitle;
    private ProgressBar pbDialogProgress;
    private TextView tvDialogProgressInfo;

    private boolean receiveHotfix = false;

    @Override
    protected int layoutId() {
        return R.layout.app_activity_main;
    }

    @Override
    protected Toolbar createToolbar() {
        return findViewById(R.id.app_toolbar);
    }

    /**
     * 热修复完成，提示用户重启应用
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onHotfixSuccess(HotfixEvent event) {
        receiveHotfix = true;
        if (hotfixDialog == null) {
            initHotfixDialog();
        }
        hotfixDialog.show();
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImportEvent(ImportEvent event) {
        if (importInfoDialog != null && !importInfoDialog.isShowing()) {
            event.stop = true;
            return;
        }
        tvDialogTitle.setText(getString(R.string.app_importing) + "「" + event.name + "」");
        if (pbDialogProgress.getMax() == 0) {
            pbDialogProgress.setMax(event.totalProgress);
        }
        pbDialogProgress.setProgress(event.curProgress);
        tvDialogProgressInfo.setText(event.curProgress + " / " + event.totalProgress);
    }

    private void initImportInfoDialog() {
        importInfoDialog = DialogManager.createImportInfoDialog(this, new DialogManager.ImportInfoDialogCallback() {
            @Override
            public void onTitle(TextView tv) {
                tvDialogTitle = tv;
            }

            @Override
            public void onProgress(ProgressBar progressBar) {
                pbDialogProgress = progressBar;
            }

            @Override
            public void onProgressInfo(TextView tv) {
                tvDialogProgressInfo = tv;
            }

            @Override
            public void onStopImport(Button btn) {
                btn.setOnClickListener(new OnClickListenerImpl() {
                    @Override
                    public void onViewClick(View v, long interval) {
                        importInfoDialog.dismiss();
                        pbDialogProgress.setMax(0);
                    }
                });
            }
        });
    }

    private void initHotfixDialog() {
        hotfixDialog = DialogManager.createDoubleBtnDialog(this, new DialogManager.DoubleBtnDialogCallback() {
            @Override
            public void onTitle(TextView tv) {
                tv.setText(R.string.app_find_update);
            }

            @Override
            public void onContent(TextView tv) {
                tv.setText(R.string.app_restart_to_update);
            }

            @Override
            public void onLeft(Button btn) {
                btn.setText(R.string.app_later);
                btn.setOnClickListener(new OnClickListenerImpl() {
                    @Override
                    public void onViewClick(View v, long interval) {
                        hotfixDialog.dismiss();
                    }
                });
            }

            @Override
            public void onRight(Button btn) {
                btn.setText(R.string.app_restart_right_now);
                btn.setOnClickListener(new OnClickListenerImpl() {
                    @Override
                    public void onViewClick(View v, long interval) {
                        final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                        if (intent != null) {
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        attachP();
        EventBus.getDefault().register(this);
        unbinder = ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
        presenter.detachV();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_REQUEST_CODE && resultCode == RESULT_OK) {
            presenter.insertPDF(data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.app_search) {
            SearchActivity.start(this);

        } else if (itemId == R.id.app_more) {
            View parent = getWindow().getDecorView();
            int x = ConvertUtils.dp2px(6);
            int y = ConvertUtils.dp2px(80);
            pwMenu.showAtLocation(parent, Gravity.TOP | Gravity.END, x, y);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (vgOperationBar.getVisibility() == View.VISIBLE) {
            finishOperation();
        } else {
            super.onBackPressed();
            if (receiveHotfix) {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }
    }

    void setOperation(IOperation fragment) {
        operation = fragment;
    }

    void startOperation() {
        vp.setScrollable(false);
        tvTitle.setText(getString(R.string.app_selected_zero));
        ibtnSelectAll.setSelected(false);
        OperationBarHelper.show(vgOperationBar);
    }

    void finishOperation() {
        vp.setScrollable(true);
        OperationBarHelper.hide(vgOperationBar);
        operation.cancelSelect();
    }

    @SuppressLint("SetTextI18n")
    void selectResult(int count, boolean selectAll) {
        ibtnDelete.setEnabled(count > 0);
        ibtnSelectAll.setSelected(selectAll);
        tvTitle.setText(getString(R.string.app_selected) + "(" + count + ")");
    }

    @Override
    public void onShowMessage(int stringId) {
        UiManager.showShort(stringId);
    }

    @Override
    public void onShowLoading() {
//        if (loadingDialog == null) {
//            loadingDialog = DialogManager.createLoadingDialog(this);
//        }
//        loadingDialog.show();
        if (importInfoDialog == null) {
            initImportInfoDialog();
        }
        importInfoDialog.show();
    }

    @Override
    public void onHideLoading() {
        importInfoDialog.dismiss();
        pbDialogProgress.setMax(0);
    }

    @Override
    public void onUpdate() {
        List<Fragment> list = getSupportFragmentManager().getFragments();
        for (Fragment fragment : list) {
            if (fragment instanceof AllFragment) {
                ((AllFragment) fragment).update();
                return;
            }
        }
    }

    @Override
    public void attachP() {
        presenter = new MainPresenter(this);
    }

    private void initView() {
        createDeleteDialog();

        initPwMenu();

        setListener();

        tabLayout.setupWithViewPager(vp);
        fragmentPagerAdapter = new MainFragmentAdapter(getSupportFragmentManager());
        vp.setAdapter(fragmentPagerAdapter);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    private void createDeleteDialog() {
        deleteDialog = DialogManager.createDeleteDialog(this, new DialogManager.DeleteDialogCallback() {
            @Override
            public void onContent(TextView tv) {
                tvDeleteDescription = tv;
            }

            @Override
            public void onLeft(Button btn) {
                btn.setOnClickListener(v -> deleteDialog.dismiss());
            }

            @Override
            public void onRight(Button btn) {
                btn.setOnClickListener(v -> {
                    deleteDialog.dismiss();
                    operation.delete();
                });
            }
        });
    }

    private void setListener() {
        ibtnCancel.setOnClickListener(v -> finishOperation());
        ibtnDelete.setOnClickListener(v -> {
            tvDeleteDescription.setText(operation.deleteDescription());
            deleteDialog.show();
        });
        ibtnSelectAll.setOnClickListener(v -> operation.selectAll(!v.isSelected()));
    }

    @SuppressLint("InflateParams")
    private void initPwMenu() {
        View pwView = LayoutInflater.from(this).inflate(R.layout.app_pw_main, null);
        TextView tvImport = pwView.findViewById(R.id.app_tv_import);
        TextView tvSettings = pwView.findViewById(R.id.app_tv_settings);
        TextView tvAbout = pwView.findViewById(R.id.app_tv_about);
        pwMenu = new PopupWindow(pwView);
        tvImport.setOnClickListener(v -> {
            PermissionUtils.permission(PermissionConstants.STORAGE)
                    .callback(new PermissionUtils.SimpleCallback() {
                        @Override
                        public void onGranted() {
//                            ArrayList<String> imported = new ArrayList<>();
//                            List<PDF> pdfList = DBHelper.queryAllPDF();
//                            for (PDF pdf : pdfList) {
//                                imported.add(pdf.getPath());
//                            }
                            SelectActivity.start(MainActivity.this, SELECT_REQUEST_CODE, (ArrayList<String>) DataManager.getPathList());
                        }

                        @Override
                        public void onDenied() {
                            UiManager.showShort(R.string.app_have_no_storage_permission);
                        }
                    })
                    .request();
            pwMenu.dismiss();
        });
        tvSettings.setOnClickListener(v -> {
            SettingsActivity.start(this);
            pwMenu.dismiss();
        });
        tvAbout.setOnClickListener(v -> {
            AboutActivity.start(this);
            pwMenu.dismiss();
        });
        pwMenu.setAnimationStyle(R.style.AppPwMenu);
        pwMenu.setFocusable(true);
        pwMenu.setOutsideTouchable(true);
        pwMenu.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        pwMenu.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pwMenu.setElevation(ConvertUtils.dp2px(4));
    }
}
