package com.krzdabrowski.airpurrr.main.current.api

import com.krzdabrowski.airpurrr.main.current.BaseModel

data class ApiModel(val current: Values?, @Transient val data: Pair<Double, Double>) : BaseModel() {
    data class Values(var values: MutableList<Map<String?, Any?>?>?)
}