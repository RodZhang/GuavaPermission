package com.rod.guavapermission

import android.os.Build
import android.support.v4.app.FragmentActivity

/**
 *
 * @author Rod
 * @date 2019/2/28
 */
class GuavaPermission(private val mActivity: FragmentActivity) {
    private val mFragment: PermissionFragment by lazy {
        var fragment = mActivity.supportFragmentManager.findFragmentByTag(PermissionFragment.TAG)
        if (fragment == null) {
            val permissionFragment = PermissionFragment()
            mActivity.supportFragmentManager.beginTransaction()
                .add(permissionFragment, PermissionFragment.TAG)
                .commitNow()
            permissionFragment
        } else {
            fragment as PermissionFragment
        }
    }

    fun doWithPermission(permission: String, callback: PermissionResultCallback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mFragment.mPermissionCallback = callback
            mFragment.requestPermissions(permission)
        }
    }

    interface PermissionResultCallback {
        fun onGranted()
        fun showRequestPermissionRationale()
        fun onDenied()
    }
}