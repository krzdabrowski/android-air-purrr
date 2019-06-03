package com.krzdabrowski.airpurrr.di

import com.krzdabrowski.airpurrr.main.PurifierHelper
import com.krzdabrowski.airpurrr.main.current.api.ApiRepository
import com.krzdabrowski.airpurrr.main.current.detector.DetectorRepository
import com.krzdabrowski.airpurrr.main.current.api.ApiService
import com.krzdabrowski.airpurrr.main.current.detector.DetectorControlService
import com.krzdabrowski.airpurrr.main.current.detector.DetectorDataService
import com.krzdabrowski.airpurrr.main.current.api.ApiViewModel
import com.krzdabrowski.airpurrr.main.current.detector.DetectorViewModel
import com.krzdabrowski.airpurrr.login.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val networkModule = module {
    single { DetectorDataService.create() }
    single { DetectorControlService.create() }
    single { ApiService.create() }
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
    viewModel { DetectorViewModel(get()) }
    viewModel { ApiViewModel(get()) }
}