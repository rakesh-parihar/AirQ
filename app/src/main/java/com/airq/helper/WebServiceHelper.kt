package com.airq.helper

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import com.airq.R
import com.airq.`interface`.WebResponseCallback
import com.airq.extension.isConnectivityAvailable
import okhttp3.*
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit


/**
 * Webservice Helper Class
 *
 * @author Rakesh
 * @since 6/26/2018.
 */
object WebServiceHelper {

    private var okhttpClient: OkHttpClient?= null
    private lateinit var context: WeakReference<Activity>

    private fun getInstance(): OkHttpClient? {
        if (okhttpClient == null) {
            val client = OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build()
            okhttpClient = client
        }
        return okhttpClient
    }

    fun  execute(context: AppCompatActivity, url: String, callback: WebResponseCallback) {
        this.context = WeakReference(context)
        if(!context.isConnectivityAvailable()){
            callback.onFailure(context.getString(R.string.msg_no_internet))
            return
        }
        getInstance()
        val request = Request.Builder()
                .url(url)
                .build()

        okhttpClient?.newCall(request)?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                context.runOnUiThread {
                    callback.onFailure(null)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                var resp: String = response.body()?.string()!!
                context.runOnUiThread {
                    callback.onSuccess(resp)
                }
            }
        })
    }


}