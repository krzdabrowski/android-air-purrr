package com.krzdabrowski.airpurrr.di

import com.krzdabrowski.airpurrr.helper.PurifierHelper
import com.krzdabrowski.airpurrr.repository.ApiRepository
import com.krzdabrowski.airpurrr.repository.DetectorRepository
import com.krzdabrowski.airpurrr.retrofit.ApiService
import com.krzdabrowski.airpurrr.retrofit.DetectorControlService
import com.krzdabrowski.airpurrr.retrofit.DetectorDataService
import com.krzdabrowski.airpurrr.viewmodel.ApiViewModel
import com.krzdabrowski.airpurrr.viewmodel.DetectorViewModel
import com.krzdabrowski.airpurrr.viewmodel.LoginViewModel
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