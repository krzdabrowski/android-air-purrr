package com.krzdabrowski.airpurrr.common

import com.krzdabrowski.airpurrr.main.PurifierHelper
import com.krzdabrowski.airpurrr.main.current.api.ApiRepository
import com.krzdabrowski.airpurrr.main.current.detector.DetectorRepository
import com.krzdabrowski.airpurrr.main.current.api.ApiService
import com.krzdabrowski.airpurrr.main.current.detector.DetectorControlService
import com.krzdabrowski.airpurrr.main.current.detector.DetectorDataService
import com.krzdabrowski.airpurrr.main.current.api.ApiViewModel
import com.krzdabrowski.airpurrr.main.current.detector.DetectorViewModel
import com.krzdabrowski.airpurrr.login.LoginViewModel
import com.krzdabrowski.airpurrr.main.current.BaseViewModel
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

val networkModule = module {
    single { DetectorDataService.create(provideOkHttpClient(3)) }
    single { DetectorControlService.create(provideOkHttpClient(10)) }
    single { ApiService.create(provideOkHttpClient(30)) }
}

val helperModule = module {
    single { PurifierHelper(get()) }
}

val repositoryModule = module {
    single { DetectorRepository(get(), get()) }
    single { ApiRepository(get()) }
}

val viewModelModule = module {
    viewModel { LoginViewModel() }
    viewModel { BaseViewModel() }
    viewModel { DetectorViewModel(get()) }
    viewModel { ApiViewModel(get()) }
}

private fun provideOkHttpClient(timeout: Long): OkHttpClient {
    return OkHttpClient.Builder()
            .connectTimeout(timeout, TimeUnit.SECONDS)
            .readTimeout(timeout, TimeUnit.SECONDS)
            .build()
}