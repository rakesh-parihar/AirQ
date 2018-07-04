package com.airq.model

import com.google.gson.annotations.SerializedName

/**
 * Base model calls to parse complete data from webservice
 *
 * @author Rakesh
 * @since 6/26/2018.
 */
data class BaseDataModel(
        @SerializedName("index_name")
        var indexName: String = "",
        var title: String = "",
        var desc: String = "",
        var created: Int = 0,
        var updated: Int = 0,
        @SerializedName("created_date")
        var createdDate: String = "",
        var active: String = "",
        var visualizable: String = "",
        @SerializedName("catalog_uuid")
        var catalogUuid: String = "",
        var source: String = "",
        @SerializedName("org_type")
        var orgType: String = "",
        var org: List<String> = listOf(),
        var sector: List<String> = listOf(),
        var field: List<Field> = listOf(),
        @SerializedName("target_bucket")
        var targetBucket: TargetBucket = TargetBucket(),
        var status: String = "",
        var message: String = "",
        var total: Int = 0,
        var count: Int = 0,
        var limit: String = "",
        var offset: String = "",
        var records: List<Record> = listOf(),
        var version: String = ""
) {
    data class TargetBucket(
            var index: String = "",
            var type: String = ""
    )

    data class Field(
            var id: String = "",
            var name: String = "",
            var type: String = ""
    )

    data class Record(
            var id: Int = 0,
            var country: String = "",
            var state: String = "",
            var city: String = "",
            var station: String = "",
            @SerializedName("last_update")
            var lastUpdate: String = "",
            @SerializedName("pollutant_id")
            var pollutantId: String = "",
            @SerializedName("pollutant_min")
            var pollutantMin: String = "",
            @SerializedName("pollutant_max")
            var pollutantMax: String = "",
            @SerializedName("pollutant_avg")
            var pollutantAvg: String = "",
            @SerializedName("pollutant_unit")
            var pollutantUnit: String = ""
    )
}