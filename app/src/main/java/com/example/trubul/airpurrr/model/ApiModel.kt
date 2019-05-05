package com.example.trubul.airpurrr.model

data class ApiModel(val current: Values?, @Transient val data: Pair<Double, Double>) : BaseModel() {
    data class Values(var values: MutableList<Map<String?, Any?>?>?)
}