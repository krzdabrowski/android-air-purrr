package com.example.trubul.airpurrr.model

data class Api(val values: MutableList<Values>) : BaseModel() {
    data class Values(var value: String, var date: String)
}