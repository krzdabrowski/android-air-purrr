package com.krzdabrowski.airpurrr.model

data class ApiModel(val current: Values?, @Transient val data: DoubleArray) : BaseModel() {
    data class Values(var values: MutableList<Map<String?, Any?>?>?)
}