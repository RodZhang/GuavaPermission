package com.rod.testpermission

import android.Manifest
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import com.baidu.location.BDLocationListener
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.location.LocationClientOption.LocationMode
import com.rod.guavapermission.GuavaPermission
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var mLocationClient: LocationClient? = null
    private val myListener = BDLocationListener() {
        location_info.text =
                "lon=${it.longitude}, lat=${it.latitude}, locType=${it.locType} addr=${it.addrStr}, city=${it.city}"
        mLocationClient?.stop()
    }
    private val mGuavaPermission by lazy { GuavaPermission(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mLocationClient = LocationClient(applicationContext)
        mLocationClient!!.registerLocationListener(myListener)
        configLocationClient()

        locate_btn.setOnClickListener { locate() }
    }

    private fun configLocationClient() {
        val option = LocationClientOption()

        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；
        option.locationMode = LocationMode.Battery_Saving

        //可选，设置返回经纬度坐标类型，默认GCJ02
        //GCJ02：国测局坐标；
        //BD09ll：百度经纬度坐标；
        //BD09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回WGS84类型坐标
        option.setCoorType("gcj02")

        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效
        option.setScanSpan(1000)

        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true
        option.isOpenGps = true

        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false
        option.isLocationNotify = true

        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)
        option.setIgnoreKillProcess(false)

        //可选，设置是否收集Crash信息，默认收集，即参数为false
        option.SetIgnoreCacheException(false)

        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false
        option.setEnableSimulateGps(false)

        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
        mLocationClient?.setLocOption(option)
    }

    private fun locate() {
        mGuavaPermission.doWithPermission(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)) { permission, granted, showRational ->
            if (!TextUtils.equals(permission, Manifest.permission.ACCESS_FINE_LOCATION)) {
                return@doWithPermission
            }
            when {
                granted -> {
                    location_info.text = "start locate..."
                    mLocationClient?.start()
                }
                showRational -> location_info.text = "give me your location, it's good for you :)"
                else -> location_info.text = "you denied location permission"
            }
        }
    }
}
