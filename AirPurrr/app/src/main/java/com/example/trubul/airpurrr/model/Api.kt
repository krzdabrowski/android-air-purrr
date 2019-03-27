package com.example.trubul.airpurrr.model

object Api {
    data class Result(val values: MutableList<Values>)
    data class Values(var value: String, var date: String)
}