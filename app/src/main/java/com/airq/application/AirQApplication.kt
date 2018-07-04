package com.airq.application

import android.app.Application
import com.airq.R
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy

/**
 * Application Class
 * @author Rakesh
 * @since 6/26/2018.
 */
class AirQApplication : Application() {

    override fun onCreate() {
        super.onCreate()

      val  formatStrategy: FormatStrategy  = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)
                .methodCount(0)
                .tag(getString(R.string.app_name))
                .build()
        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))
    }
}