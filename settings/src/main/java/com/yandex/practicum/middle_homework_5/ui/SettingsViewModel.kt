package com.yandex.practicum.middle_homework_5.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.practicum.middle_homework_5.data_store.SettingContainer
import com.yandex.practicum.middle_homework_5.ui.contract.DataStoreService
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val dataStoreService: DataStoreService
) : ViewModel() {

    fun saveSetting(periodic: Long, delayed: Long) {
        viewModelScope.launch {
            dataStoreService.saveSetting(periodic = periodic, delayed = delayed)
        }
    }

    fun getCurrentSetting(): SettingContainer {
        return dataStoreService.settingData.value
    }
}