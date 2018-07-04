package com.airq.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

/**
 * DataBase Helper Class
 *
 * @author Rakesh
 * @since 6/27/2018.
 */
class AppDataBaseHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, DATABASE, null, 1) {

    /*
        Table data
     */
    val TABLE = "air_data"
    val ID = "id"
    val COUNTRY = "country"
    val STATE = "state"
    val CITY = "city"
    val STATION = "station"
    val LAST_UPDATE = "last_update"
    val POLLUTANT_ID = "pollutant_id"
    val POLLUTANT_MIN = "pollutant_min"
    val POLLUTANT_MAX = "pollutant_max"
    val POLLUTANT_AVG = "pollutant_avg"

    companion object {

        val DATABASE = "AirDB"

        private var instance: AppDataBaseHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): AppDataBaseHelper {
            if (instance == null) {
                instance = AppDataBaseHelper(ctx.getApplicationContext())
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(TABLE, true,
                ID to INTEGER + PRIMARY_KEY + UNIQUE,
                COUNTRY to TEXT,
                STATE to TEXT,
                CITY to TEXT,
                STATION to TEXT,
                LAST_UPDATE to TEXT,
                POLLUTANT_ID to TEXT,
                POLLUTANT_MIN to INTEGER,
                POLLUTANT_MAX to INTEGER,
                POLLUTANT_AVG to INTEGER)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.dropTable(TABLE, true)
    }

}

