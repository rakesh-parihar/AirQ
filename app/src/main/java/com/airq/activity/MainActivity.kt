package com.airq.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.view.View
import com.airq.R
import com.airq.adapter.GenericAdapter
import com.airq.constants.*
import com.airq.model.AirDataModel
import com.airq.model.GenericDataModel
import com.orhanobut.logger.Logger
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.db.select
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.text.SimpleDateFormat


/**
 * MainActivity class
 *
 * @author Rakesh
 * @since 6/26/2018.
 */
class MainActivity : BaseAppCompatActivity() {

    /*
        Data Declarations
     */
    private val statesList: ArrayList<GenericDataModel> = arrayListOf()
    private val cityList: ArrayList<GenericDataModel> = arrayListOf()
    private val stationList: ArrayList<GenericDataModel> = arrayListOf()
    private val airDataList: ArrayList<AirDataModel> = arrayListOf()
    private lateinit var airModel: AirDataModel
    /*
        UI Declarations
     */
    private lateinit var stateAdapter: RecyclerView.Adapter<*>
    private lateinit var cityAdapter: RecyclerView.Adapter<*>
    private lateinit var stationAdapter: RecyclerView.Adapter<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()
        getStateData()
    }

    /**
     * Method to Init UI elements
     */
    private fun initUI() {
        stateAdapter = GenericAdapter(this, statesList, View.OnClickListener {
            val pos: Int = it.tag.toString().toInt()
            Logger.d("state clicked= $pos")
            getCityData(statesList[pos].name)
        })

        cityAdapter = GenericAdapter(this, cityList, View.OnClickListener {
            val pos: Int = it.tag.toString().toInt()
            Logger.d("city clicked= $pos")
            getStationData(cityList[pos].name)
        })

        stationAdapter = GenericAdapter(this, stationList, View.OnClickListener {
            val pos: Int = it.tag.toString().toInt()
            Logger.d("station clicked= $pos")
            getAirData(stationList[pos].name)
        })


        rvState.apply {
            setHasFixedSize(true)
            this.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = this@MainActivity.stateAdapter
        }


        rvCity.apply {
            setHasFixedSize(true)
            this.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = this@MainActivity.cityAdapter
        }


        rvStation.apply {
            setHasFixedSize(false)
            this.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = this@MainActivity.stationAdapter
        }

    }


    /**
     * Method to get State List
     */
    private fun getStateData() {
        doAsync {

            database.use {
                select(database.TABLE, database.STATE).distinct().exec {
                    this.moveToFirst()
                    statesList.clear()
                    do {
                        statesList.add(GenericDataModel(this.getString(0)))
                    } while (this.moveToNext())

                    Logger.d("states=$statesList")
                    statesList[0].isSelected = true

                }
            }
            uiThread {
                (stateAdapter as GenericAdapter).notifyDataSetChanged()
                getCityData(statesList[0].name)
            }
        }

    }

    /**
     * Method to get City List
     */
    private fun getCityData(state: String) {
        doAsync {
            database.use {
                select(database.TABLE, database.STATE, database.CITY)
                        .whereArgs("(${database.STATE} = {state})",
                                "state" to state)
                        .distinct()
                        .exec {
                            this.moveToFirst()
                            cityList.clear()
                            do {
                                cityList.add(GenericDataModel(this.getString(1)))
                            } while (this.moveToNext())

                            Logger.d("cities=$cityList")

                            cityList[0].isSelected = true
                        }
            }

            uiThread {
                (cityAdapter as GenericAdapter).notifyDataSetChanged()
                getStationData(cityList[0].name)
            }

        }
    }

    /**
     * Method to get stations List
     */
    private fun getStationData(city: String) {
        doAsync {
            database.use {
                select(database.TABLE, database.CITY, database.STATION)
                        .whereArgs("(${database.CITY} = {city})",
                                "city" to city)
                        .distinct()
                        .exec {
                            this.moveToFirst()
                            stationList.clear()
                            do {
                                stationList.add(GenericDataModel(this.getString(1)))
                            } while (this.moveToNext())
                            Logger.d("stations=$stationList")
                            stationList[0].isSelected = true
                        }
            }

            uiThread {
                (stationAdapter as GenericAdapter).notifyDataSetChanged()
                getAirData(stationList[0].name)
            }
        }
    }


    /**
     * Method to update UI with Air Quality Data
     */
    private fun updateAirData() {
        var aqi = calculateAQI()
        Logger.d("AQI = $aqi")

        txtResult.text = "${resources.getString(R.string.str_aqi)} = $aqi"
        when {
            aqi <= AQI_CAT1 -> {
                txtRemark.setTextColor(this@MainActivity.resources.getColor(R.color.colorAqiLevel_1))
                txtRemark.text = this@MainActivity.resources.getString(R.string.remark_L1)
                txtEffect.text = this@MainActivity.resources.getString(R.string.impact_L1)

            }
            aqi <= AQI_CAT2 -> {
                txtRemark.setTextColor(this@MainActivity.resources.getColor(R.color.colorAqiLevel_2))
                txtRemark.text = this@MainActivity.resources.getString(R.string.remark_L2)
                txtEffect.text = this@MainActivity.resources.getString(R.string.impact_L2)

            }
            aqi <= AQI_CAT3 -> {
                txtRemark.setTextColor(this@MainActivity.resources.getColor(R.color.colorAqiLevel_3))
                txtRemark.text = this@MainActivity.resources.getString(R.string.remark_L3)
                txtEffect.text = this@MainActivity.resources.getString(R.string.impact_L3)

            }
            aqi <= AQI_CAT4 -> {
                txtRemark.setTextColor(this@MainActivity.resources.getColor(R.color.colorAqiLevel_4))
                txtRemark.text = this@MainActivity.resources.getString(R.string.remark_L4)
                txtEffect.text = this@MainActivity.resources.getString(R.string.impact_L4)


            }
            aqi <= AQI_CAT5 -> {
                txtRemark.setTextColor(this@MainActivity.resources.getColor(R.color.colorAqiLevel_5))
                txtRemark.text = this@MainActivity.resources.getString(R.string.remark_L5)
                txtEffect.text = getString(R.string.impact_L5)

            }
            aqi > AQI_CAT5 -> {
                txtRemark.setTextColor(this@MainActivity.resources.getColor(R.color.colorAqiLevel_6))
                txtRemark.text = this@MainActivity.resources.getString(R.string.remark_L6)
                txtEffect.text = getString(R.string.impact_L6)

            }

        }

        var dt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(airModel.datetime)
        txtLastUpdate.text = "${resources.getString(R.string.str_lastupdated)} ${SimpleDateFormat("EEE, d MMM yyyy hh:mm a").format(dt)}"

        txtStation.text = stationList.filter { it.isSelected }[0].name
        var cityName = cityList.filter { it.isSelected }[0].name


        if (aqi == -1) {
            txtResult.text = getString(R.string.str_na)
            txtRemark.text = getString(R.string.str_na)
            txtEffect.text = getString(R.string.str_na)
        }

        Picasso.get().load(generateMapUrl(cityName)).into(imgMap)
    }


    /**
     * Method to generate google Static map url
     */
    private fun generateMapUrl(city: String): String {
        //Calculating screen width to determine appropriate size of map to show
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels.toDouble()
        val imgWidth = 720.toDouble()

        val sb = StringBuilder()
        sb.append("https://maps.googleapis.com/maps/api/staticmap?center=")
        sb.append(city)
        sb.append("&zoom=10&scale=")
        if (imgWidth < width)
            sb.append(Math.round(width / imgWidth).toInt())
        else
            sb.append("false")
        sb.append("&size=")
        sb.append("${imgWidth.toInt()}x${imgWidth.toInt()}")
        sb.append("&maptype=hybrid&format=png&visual_refresh=true&markers=size:mid%7Ccolor:0xff0000%7Clabel:%7C")
        sb.append(city)
        sb.append("&key=AIzaSyC8GLs4n-c8i5hnxVJO2D-T3SS85pP4h0Q")
        Logger.d("map url=${sb.toString()}")
        return sb.toString()
    }

    /**
     * Method to get Air List
     */
    private fun getAirData(st: String) {
        doAsync {
            database.use {
                select(database.TABLE, database.STATION, database.POLLUTANT_ID, database.POLLUTANT_AVG, database.POLLUTANT_MIN, database.POLLUTANT_MAX, database.LAST_UPDATE)
                        .whereArgs("(${database.STATION} = {station})",
                                "station" to st)
                        .exec {
                            airDataList.clear()
                            this.moveToFirst()
                            Logger.d("AirDATa size= ${this.count}")
                            do {
                                val model = AirDataModel(pollutantId = this.getString(1), pollutantAvg = this.getString(2), pollutantMin = this.getString(3), pollutantMax = this.getString(4), datetime = this.getString(5))

                                if (model.pollutantAvg.toIntOrNull() != null)
                                    airDataList.add(model)

                            } while (this.moveToNext())

                        }
            }
            uiThread {
                updateAirData()
            }
        }
    }


    /**
     * Method to calculate AQI Value
     */
    private fun calculateAQI(): Int {

        if (airDataList.size < 3)
            return -1

        for (model in airDataList) {
            when (model.pollutantId) {
                PTYPE_PM10 -> {
                    model.aqi = calculatePM10(model)
                    model.check = model.aqi != -1
                }
                PTYPE_PM25 -> {
                    model.aqi = calculatePM25(model)
                    model.check = model.aqi != -1
                }
                PTYPE_SO2 -> {
                    model.aqi = calculateSO2(model)
                    model.check = model.aqi != -1
                }
                PTYPE_NO2 -> {
                    model.aqi = calculateNO2(model)
                    model.check = model.aqi != -1
                }
//                PTYPE_CO -> {
//                    model.aqi = calculateCO(model)
//                    model.check = model.aqi != -1
//                }
                PTYPE_OZ -> {
                    model.aqi = calculateOZ(model)
                    model.check = model.aqi != -1
                }
                PTYPE_NH3 -> {
                    model.aqi = calculateNH3(model)
                    model.check = model.aqi != -1
                }
            }
        }

        airModel = airDataList.filter { it.check && it.aqi >= 0 }.sortedByDescending { it.aqi }[0]
        Logger.d("airmodel===$airModel")

        if (airModel != null)
            return airModel.aqi
        return 0
    }


    /**
     * Method to calculate PM10 sub value
     */
    private fun calculatePM10(model: AirDataModel): Int {
        var subval = -1
        try {
            val input = model.pollutantAvg.toInt()
            subval = when {
                input <= PM10_CAT1 -> input
                input <= PM10_CAT2 -> AQI_CAT1 + ((input - PM10_CAT1) * (AQI_CAT1 / PM10_CAT1))
                input <= PM10_CAT3 -> AQI_CAT2 + ((input - PM10_CAT2) * ((AQI_CAT2 - AQI_CAT1) / (PM10_CAT2 - PM10_CAT1)))
                input <= PM10_CAT4 -> AQI_CAT3 + ((input - PM10_CAT3) * ((AQI_CAT3 - AQI_CAT2) / (PM10_CAT3 - PM10_CAT2)))
                input <= PM10_CAT5 -> AQI_CAT4 + ((input - PM10_CAT4) * ((AQI_CAT4 - AQI_CAT3) / (PM10_CAT4 - PM10_CAT3)))
                else -> AQI_CAT6 + ((input - PM10_CAT5) * ((AQI_CAT5 - AQI_CAT4) / (PM10_CAT5 - PM10_CAT4)))
            }
            Logger.d("PM10 ${model.pollutantAvg}  =  $subval")
        } finally {
            return subval
        }
    }

    /**
     * Method to calculate PM2.5 sub value
     */
    private fun calculatePM25(model: AirDataModel): Int {

        var subval = -1
        try {
            val input = model.pollutantAvg.toInt()
            subval = when {
                input <= PM25_CAT1 -> input
                input <= PM25_CAT2 -> AQI_CAT1 + ((input - PM25_CAT1) * (AQI_CAT1 / PM25_CAT1))
                input <= PM25_CAT3 -> AQI_CAT2 + ((input - PM25_CAT2) * ((AQI_CAT2 - AQI_CAT1) / (PM25_CAT2 - PM25_CAT1)))
                input <= PM25_CAT4 -> AQI_CAT3 + ((input - PM25_CAT3) * ((AQI_CAT3 - AQI_CAT2) / (PM25_CAT3 - PM25_CAT2)))
                input <= PM25_CAT5 -> AQI_CAT4 + ((input - PM25_CAT4) * ((AQI_CAT4 - AQI_CAT3) / (PM25_CAT4 - PM25_CAT3)))
                else -> AQI_CAT6 + ((input - PM25_CAT5) * ((AQI_CAT5 - AQI_CAT4) / (PM25_CAT5 - PM25_CAT4)))
            }
            Logger.d("PM2.5 ${model.pollutantAvg}  =  $subval")
        } finally {
            return subval
        }
    }

    /**
     * Method to calculate SO2 sub value
     */
    private fun calculateSO2(model: AirDataModel): Int {

        var subval = -1
        try {
            val input = model.pollutantAvg.toInt()
            subval = when {
                input <= SO2_CAT1 -> input
                input <= SO2_CAT2 -> AQI_CAT1 + ((input - SO2_CAT1) * (AQI_CAT1 / SO2_CAT1))
                input <= SO2_CAT3 -> AQI_CAT2 + ((input - SO2_CAT2) * ((AQI_CAT2 - AQI_CAT1) / (SO2_CAT2 - SO2_CAT1)))
                input <= SO2_CAT4 -> AQI_CAT3 + ((input - SO2_CAT3) * ((AQI_CAT3 - AQI_CAT2) / (SO2_CAT3 - SO2_CAT2)))
                input <= SO2_CAT5 -> AQI_CAT4 + ((input - SO2_CAT4) * ((AQI_CAT4 - AQI_CAT3) / (SO2_CAT4 - SO2_CAT3)))
                else -> AQI_CAT6 + ((input - SO2_CAT5) * ((AQI_CAT5 - AQI_CAT4) / (SO2_CAT5 - SO2_CAT4)))
            }
            Logger.d("SO2 ${model.pollutantAvg}  =  $subval")
        } finally {
            return subval
        }
    }

    /**
     * Method to calculate NO2 sub value
     */
    private fun calculateNO2(model: AirDataModel): Int {

        var subval = -1
        try {
            val input = model.pollutantAvg.toInt()
            subval = when {
                input <= NO2_CAT1 -> input
                input <= NO2_CAT2 -> AQI_CAT1 + ((input - NO2_CAT1) * (AQI_CAT1 / NO2_CAT1))
                input <= NO2_CAT3 -> AQI_CAT2 + ((input - NO2_CAT2) * ((AQI_CAT2 - AQI_CAT1) / (NO2_CAT2 - NO2_CAT1)))
                input <= NO2_CAT4 -> AQI_CAT3 + ((input - NO2_CAT3) * ((AQI_CAT3 - AQI_CAT2) / (NO2_CAT3 - NO2_CAT2)))
                input <= NO2_CAT5 -> AQI_CAT4 + ((input - NO2_CAT4) * ((AQI_CAT4 - AQI_CAT3) / (NO2_CAT4 - NO2_CAT3)))
                else -> AQI_CAT6 + ((input - NO2_CAT5) * ((AQI_CAT5 - AQI_CAT4) / (NO2_CAT5 - NO2_CAT4)))
            }
            Logger.d("NO2 ${model.pollutantAvg}  =  $subval")
        } finally {
            return subval
        }
    }


    /**
     * Method to calculate CO sub value
     */
    private fun calculateCO(model: AirDataModel): Int {

        var subval = -1
        try {
            val input = model.pollutantAvg.toInt()
            subval = when {
                input <= CO_CAT1 -> input
                input <= CO_CAT2 -> AQI_CAT1 + ((input - CO_CAT1) * (AQI_CAT1 / CO_CAT1))
                input <= CO_CAT3 -> AQI_CAT2 + ((input - CO_CAT2) * ((AQI_CAT2 - AQI_CAT1) / (CO_CAT2 - CO_CAT1)))
                input <= CO_CAT4 -> AQI_CAT3 + ((input - CO_CAT3) * ((AQI_CAT3 - AQI_CAT2) / (CO_CAT3 - CO_CAT2)))
                input <= CO_CAT5 -> AQI_CAT4 + ((input - CO_CAT4) * ((AQI_CAT4 - AQI_CAT3) / (CO_CAT4 - CO_CAT3)))
                else -> AQI_CAT6 + ((input - CO_CAT5) * ((AQI_CAT5 - AQI_CAT4) / (CO_CAT5 - CO_CAT4)))
            }
            Logger.d("CO ${model.pollutantAvg}  =  $subval")
        } finally {
            return subval
        }
    }


    /**
     * Method to calculate OZ sub value
     */
    private fun calculateOZ(model: AirDataModel): Int {

        var subval = -1
        try {
            val input = model.pollutantAvg.toInt()
            subval = when {
                input <= OZ_CAT1 -> input
                input <= OZ_CAT2 -> AQI_CAT1 + ((input - OZ_CAT1) * (AQI_CAT1 / OZ_CAT1))
                input <= OZ_CAT3 -> AQI_CAT2 + ((input - OZ_CAT2) * ((AQI_CAT2 - AQI_CAT1) / (OZ_CAT2 - OZ_CAT1)))
                input <= OZ_CAT4 -> AQI_CAT3 + ((input - OZ_CAT3) * ((AQI_CAT3 - AQI_CAT2) / (OZ_CAT3 - OZ_CAT2)))
                input <= OZ_CAT5 -> AQI_CAT4 + ((input - OZ_CAT4) * ((AQI_CAT4 - AQI_CAT3) / (OZ_CAT4 - OZ_CAT3)))
                else -> AQI_CAT6 + ((input - OZ_CAT5) * ((AQI_CAT5 - AQI_CAT4) / (OZ_CAT5 - OZ_CAT4)))
            }
            Logger.d("OZ ${model.pollutantAvg}  =  $subval")
        } finally {
            return subval
        }
    }


    /**
     * Method to calculate NH3 sub value
     */
    private fun calculateNH3(model: AirDataModel): Int {
        var subval = -1
        try {
            val input = model.pollutantAvg.toInt()
            subval = when {
                input <= NH3_CAT1 -> input
                input <= NH3_CAT2 -> AQI_CAT1 + ((input - NH3_CAT1) * (AQI_CAT1 / NH3_CAT1))
                input <= NH3_CAT3 -> AQI_CAT2 + ((input - NH3_CAT2) * ((AQI_CAT2 - AQI_CAT1) / (NH3_CAT2 - NH3_CAT1)))
                input <= NH3_CAT4 -> AQI_CAT3 + ((input - NH3_CAT3) * ((AQI_CAT3 - AQI_CAT2) / (NH3_CAT3 - NH3_CAT2)))
                input <= NH3_CAT5 -> AQI_CAT4 + ((input - NH3_CAT4) * ((AQI_CAT4 - AQI_CAT3) / (NH3_CAT4 - NH3_CAT3)))
                else -> AQI_CAT6 + ((input - NH3_CAT5) * ((AQI_CAT5 - AQI_CAT4) / (NH3_CAT5 - NH3_CAT4)))
            }
            Logger.d("NH3 ${model.pollutantAvg}  =  $subval")
        } finally {
            return subval
        }
    }
}

