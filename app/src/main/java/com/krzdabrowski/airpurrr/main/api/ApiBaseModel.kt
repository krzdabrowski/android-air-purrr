package com.krzdabrowski.airpurrr.main.api

data class ApiBaseModel(val current: ApiCurrentModel.Data?, val forecast: List<ApiForecastModel.Data?>?)