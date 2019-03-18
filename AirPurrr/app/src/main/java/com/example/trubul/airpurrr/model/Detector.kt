package com.example.trubul.airpurrr.model

object Detector {
    data class Result(val workstate: String, val values: Values)
    data class Values(val pm25: Double, val pm10: Double)
}