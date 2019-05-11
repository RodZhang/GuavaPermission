package com.rod.guavapermission

import android.app.Fragment
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, callback)
        } else {
            permissions.forEach { callback(it, true, false) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestPermissions(permissions: Array<String>, callback: PermissionCallback) {
        if (mRequestCode + 1 >= Int.MAX_VALUE) {
            Log.w(TAG, "ignore")
            return
        }

        mRequestCode++
        mCallbackMap.put(mRequestCode, callback)
        requestPermissions(permissions, mRequestCode)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val callback = mCallbackMap[requestCode] ?: return

        permissions.forEachIndexed { index, permission ->
            val granted = grantResults[index] == PackageManager.PERMISSION_GRANTED
            val showRational = shouldShowRequestPermissionRationale(permission)

            callback(permission, granted, showRational)
        }

        mCallbackMap.remove(requestCode)
    }

}