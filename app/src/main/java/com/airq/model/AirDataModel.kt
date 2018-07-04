package com.airq.model

/**
 * Model class for Air Quality Index data
 *
 * @author Rakesh
 * @since 7/2/2018.
 */
data class AirDataModel(var datetime: String, var pollutantId: String, var pollutantAvg: String, var pollutantMin: String, var pollutantMax: String, var aqi: Int = 0, var check: Boolean = true)

