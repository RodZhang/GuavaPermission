package com.rod.guavapermission

import android.app.Fragment
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.util.Log
import android.util.SparseArray

/**
 *
 * @author Rod
 * @date 2019/2/28
 */
internal class PermissionFragment : Fragment() {

    companion object {
        const val TAG = "PermissionFragment"
    }

    private val mCallbackMap = SparseArray<PermissionCallback>()
    private var mRequestCode = 1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    fun withPermission(permissions: Array<String>, callback: PermissionCallback) {
        if (activity == null || activity.isFinishing) {
            Log.e(TAG, "withPermission, activity is invalid")
            return
        }

        val notGrantedPermissions = ArrayList<String>()
        permissions.forEach {
            if (ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED) {
                callback(it, true, false)
            } else {
                notGrantedPermissions.add(it)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(notGrantedPermissions.toTypedArray(), callback)
        } else {
            notGrantedPermissions.forEach { callback(it, true, false) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestPermissions(permissions: Array<String>, callback: PermissionCallback) {
        if (mRequestCode + 1 >= Int.MAX_VALUE || permissions.isEmpty()) {
            Log.w(TAG, "ignore")
            return
        }

        mCallbackMap.put(mRequestCode, callback)
        requestPermissions(permissions, mRequestCode)
        mRequestCode++
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val callback = mCallbackMap[requestCode] ?: return

        permissions.forEachIndexed { index, permission ->
            val granted = grantResults[index] == PackageManager.PERMISSION_GRANTED
            // 需要显示请求权限的原因
            // 如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true
            // 如果用户在过去拒绝了权限请求，并在权限请求系统对话框中选择了 Don't ask again 选项，此方法将返回 false。
            // 如果设备规范禁止应用具有该权限，此方法也会返回 false。
            val showRational = shouldShowRequestPermissionRationale(permission)

            callback(permission, granted, showRational)
        }

        mCallbackMap.remove(requestCode)
    }

}