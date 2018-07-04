package com.airq.activity

import android.os.Bundle
import android.os.Handler
import com.airq.R
import com.airq.`interface`.WebResponseCallback
import com.airq.constants.API_URL
import com.airq.constants.PREFS_KEY_LAST_SYNC
import com.airq.extension.getPrefs
import com.airq.extension.isSyncNeeded
import com.airq.helper.WebServiceHelper
import com.airq.model.BaseDataModel
import com.google.gson.GsonBuilder
import com.orhanobut.logger.Logger
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import org.jetbrains.anko.intentFor

/**
 * Splash Activity class
 *
 * @author Rakesh
 * @since 6/26/2018.
 */
class SplashActivity : BaseAppCompatActivity() {

    private val DELAY: Long = 1000 //ms

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (isSyncNeeded())
            fetchData()
        else {
            Handler().postDelayed({
                navigate()
            }, DELAY)
        }

    }

    /**
     * Method to navigate to MainActivity
     */
    private fun navigate() {
        startActivity(intentFor<MainActivity>())
        finish()
    }

    /**
     * Method to get Data from server and save to database
     */
    private fun fetchData() {
        val webHelper = WebServiceHelper
        webHelper.execute(this, API_URL, object : WebResponseCallback {
            override fun onSuccess(result: Any?) {
                Logger.d("onSuccess : $result")
                val gson = GsonBuilder().create()
                val data: BaseDataModel = gson.fromJson(result.toString(), BaseDataModel::class.java)

                getPrefs().edit().putLong(PREFS_KEY_LAST_SYNC, System.currentTimeMillis()).apply()
                bg {
                    database.use {
                        delete(database.TABLE, "")

                        for (model in data.records) {
                            insert(database.TABLE,
                                    database.ID to model.id,
                                    database.COUNTRY to model.country,
                                    database.STATE to model.state,
                                    database.CITY to model.city,
                                    database.STATION to model.station,
                                    database.LAST_UPDATE to model.lastUpdate,
                                    database.POLLUTANT_ID to model.pollutantId,
                                    database.POLLUTANT_MIN to model.pollutantMin,
                                    database.POLLUTANT_MAX to model.pollutantMax,
                                    database.POLLUTANT_AVG to model.pollutantAvg
                            )
                        }


                        select(database.TABLE).exec {
                            Logger.d("count=" + this.count)


                            async(UI) {
                                navigate()
                            }
                        }
                    }
                }
            }

            override fun onFailure(reason: String?) {
                Logger.d("onFailure : $reason")
                navigate()
            }

        })
    }
}
