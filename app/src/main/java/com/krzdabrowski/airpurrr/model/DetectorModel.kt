package com.krzdabrowski.airpurrr.model

data class DetectorModel(val workstate: String, val values: Values?) : BaseModel() {
    data class Values(val pm25: Double, val pm10: Double)
}