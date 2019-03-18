package com.example.trubul.airpurrr.model

object Api {
    data class Result(val values: List<Values>)
    data class Values(val date: String, val value: Double)
}