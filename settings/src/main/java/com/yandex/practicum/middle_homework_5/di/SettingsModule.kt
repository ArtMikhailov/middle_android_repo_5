package com.yandex.practicum.middle_homework_5.di

import com.yandex.practicum.middle_homework_5.data_store.DataStoreServiceImpl
import com.yandex.practicum.middle_homework_5.ui.SettingsViewModel
import com.yandex.practicum.middle_homework_5.ui.contract.DataStoreService
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    single<DataStoreService> { DataStoreServiceImpl(androidApplication()) }
    viewModel { SettingsViewModel(get()) }
}