package com.krzdabrowski.airpurrr.main.current

import android.content.Context

abstract class BaseModel {
    abstract fun getSource(context: Context): String

    abstract fun getDataPercentage(context: Context, type: String): String

    abstract fun getDataUgm3(context: Context, type: String): String
}