package com.github.keyboard3.developerinterview.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.github.keyboard3.developerinterview.R;
import com.github.keyboard3.developerinterview.base.IProgressDialog;
import com.wang.avi.AVLoadingIndicatorView;
import com.werb.mediautilsdemo.CustomPermissionChecker;

/**
 * 基类的Activity 让子类更加专注的实现自己的业务
 *
 * @author keyboard3
 * @date 2017/9/3
 */
public class BaseActivity extends AppCompatActivity implements IProgressDialog {

    private ProgressDialog mProgressDialog;
    private AVLoadingIndicatorView mAdvanceProgressView;
    private Toolbar mToolbar;

    private CustomPermissionChecker mPermissionChecker;
    private boolean mAdvanceDialogToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (hasActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void setTitle(@StringRes int titleRid) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(titleRid);
    }

    public boolean hasActionBar() {
        return true;
    }

    protected Toolbar findToolbar() {
        if (mToolbar == null) {
            mToolbar = findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
        }
        return mToolbar;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (hasActionBar()) {
            if (item.getItemId() == android.R.id.home) {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    protected void toggleDialogAdvance(boolean toggle) {
        mAdvanceDialogToggle = toggle;

    }

    @Override
    public void showDialog() {
        if (mAdvanceDialogToggle && mAdvanceProgressView == null) {
            mAdvanceProgressView = findViewById(R.id.avi);
        } else if (!mAdvanceDialogToggle && mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.com_loading));
            mProgressDialog.setIndeterminate(true);
        }
        if (mAdvanceDialogToggle) {
            mAdvanceProgressView.show();
        } else {
            mProgressDialog.show();
        }
    }

    @Override
    public void hideDialog() {
        if (mAdvanceDialogToggle && mAdvanceProgressView != null) {
            if (mAdvanceProgressView == null) {
                return;
            }
            mAdvanceProgressView.hide();
        } else {
            if (mProgressDialog == null) {
                return;
            }
            mProgressDialog.hide();
        }
    }

    @Override
    public boolean isShowing() {
        return mProgressDialog.isShowing();
    }


    protected boolean checkPermission(@NonNull String[] permissions, @NonNull int requestCode) {
        if (mPermissionChecker == null) {
            mPermissionChecker = new CustomPermissionChecker(this);
        }

        if (mPermissionChecker.isLackPermissions(permissions)) {
            mPermissionChecker.requestPermissions(requestCode);
            return true;
        }
        return false;
    }

    protected boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        if (!mPermissionChecker.hasAllPermissionsGranted(grantResults)) {
            showPermissionDialog();
            return false;
        }
        return true;
    }

    protected void showPermissionDialog() {
        mPermissionChecker.showDialog();
    }
}