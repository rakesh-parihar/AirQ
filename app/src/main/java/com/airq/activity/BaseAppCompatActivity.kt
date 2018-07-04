package com.airq.activity

import android.content.Context
import android.support.v7.app.AppCompatActivity
import com.airq.db.AppDataBaseHelper

/**
 * Base AppCompatActivity class
 *
 * @author Rakesh
 * @since 6/26/2018.
 */
open class BaseAppCompatActivity : AppCompatActivity() {

    // Access property for Context
    val Context.database: AppDataBaseHelper
        get() = AppDataBaseHelper.getInstance(applicationContext)

}