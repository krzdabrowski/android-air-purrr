package com.krzdabrowski.airpurrr.main.current

import android.content.Context

abstract class BaseModel {
    open fun getDataPercentage(context: Context, type: String): String {
        return ""
    }
}