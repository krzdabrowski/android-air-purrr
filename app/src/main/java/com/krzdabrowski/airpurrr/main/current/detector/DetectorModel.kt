package com.krzdabrowski.airpurrr.main.current.detector

import com.krzdabrowski.airpurrr.main.current.BaseModel

data class DetectorModel(val workstate: String, val values: Values?) : BaseModel() {
    data class Values(val pm25: Double, val pm10: Double)
}