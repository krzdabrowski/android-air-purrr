package com.example.trubul.airpurrr.di

import com.example.trubul.airpurrr.repository.ApiRepository
import com.example.trubul.airpurrr.repository.DetectorRepository
import com.example.trubul.airpurrr.retrofit.ApiService
import com.example.trubul.airpurrr.retrofit.DetectorControlService
import com.example.trubul.airpurrr.retrofit.DetectorDataService
import com.example.trubul.airpurrr.viewmodel.ApiViewModel
import com.example.trubul.airpurrr.viewmodel.DetectorViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module



val networkModule = module {
    single { DetectorDataService.create() }
    single { DetectorControlService.create() }
    single { ApiService.create() }
}

val repositoryModule = module {
    single { DetectorRepository(get(), get()) }
    single { ApiRepository(get()) }
}

val viewModelModule = module {
    viewModel { DetectorViewModel(get()) }
    viewModel { ApiViewModel(get()) }
}