package com.rod.guavapermission

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat

/**
 *
 * @author Rod
 * @date 2019/2/28
 */
internal class PermissionFragment: Fragment() {

    companion object {
        const val TAG = "PermissionFragment"
        const val REQUEST_CODE = 1427
    }

    var mPermissionCallback: GuavaPermission.PermissionResultCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    fun requestPermissions(permission: String) {
        val act = activity
        if (act == null || act.isFinishing) {
            return
        }
        if (ContextCompat.checkSelfPermission(act, permission)
            == PackageManager.PERMISSION_GRANTED) {
            mPermissionCallback?.onGranted()
            return
        }

        if (shouldShowRequestPermissionRationale(permission)) {
            // 需要显示请求权限的原因
            // 如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true
            // 如果用户在过去拒绝了权限请求，并在权限请求系统对话框中选择了 Don't ask again 选项，此方法将返回 false。
            // 如果设备规范禁止应用具有该权限，此方法也会返回 false。
            mPermissionCallback?.showRequestPermissionRationale()
            return
        }
        requestPermissions(arrayOf(permission), REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPermissionCallback?.onGranted()
                } else {
                    mPermissionCallback?.onDenied()
                }
            }
        }
    }
}