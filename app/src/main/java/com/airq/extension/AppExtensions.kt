package com.airq.extension

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import com.airq.constants.APP_PREFS
import com.airq.constants.MIN_SYNC_DURATION
import com.airq.constants.PREFS_KEY_LAST_SYNC
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton

/**
 * Extension class to have all the extension methods of the application
 *
 * @author Rakesh
 * @since 6/26/2018.
 */


/**
 * Method to check Internet Connectivity
 */
fun AppCompatActivity.isConnectivityAvailable(): Boolean {
    val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnectedOrConnecting
}


/**
 * Method to show alert dialog
 */
fun AppCompatActivity.showDialog(msg: String, onclick: DialogInterface.OnClickListener? = null) {
    alert(msg) {
        okButton {
            onclick
        }

    }.show()
}

/**
 * Method to get Instance of shared Preferences
 */
fun AppCompatActivity.getPrefs(): SharedPreferences {
    return getSharedPreferences(APP_PREFS, Activity.MODE_PRIVATE)
}


/**
 * Method checks for last sync time and let app know if needed get data from server
 */
fun AppCompatActivity.isSyncNeeded(): Boolean {
    val last = getPrefs().getLong(PREFS_KEY_LAST_SYNC, 0)
    val now = System.currentTimeMillis()
    val diff = now - last
    return diff >= MIN_SYNC_DURATION
}





