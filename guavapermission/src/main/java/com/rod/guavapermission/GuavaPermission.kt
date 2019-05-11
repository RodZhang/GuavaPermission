package com.rod.guavapermission

import android.support.v4.app.FragmentActivity

/**
 *
 * @author Rod
 * @date 2019/2/28
 */
class GuavaPermission(private val mActivity: FragmentActivity) {
    private val mFragment: PermissionFragment by lazy {
        val fragment = mActivity.fragmentManager.findFragmentByTag(PermissionFragment.TAG)
        if (fragment == null) {
            val permissionFragment = PermissionFragment()
            mActivity.fragmentManager.beginTransaction()
                .add(permissionFragment, PermissionFragment.TAG)
                .commit()
            mActivity.fragmentManager.executePendingTransactions()
            permissionFragment
        } else {
            fragment as PermissionFragment
        }
    }

    fun doWithPermission(permissions: Array<String>, callback: PermissionCallback) {
        mFragment.withPermission(permissions, callback)
    }

}

typealias PermissionCallback = (permission: String, granted: Boolean, showRational: Boolean) -> Unit