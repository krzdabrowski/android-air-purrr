package com.krzdabrowski.airpurrr.main.detector

enum class DetectorWorkstate(val state: String) {
    SLEEPING("WorkStates.Sleeping"),
    MEASURING("WorkStates.Measuring");

    companion object {
        private val values = values()
        fun getByValue(value: String) = values.firstOrNull { it.state == value }
    }
}