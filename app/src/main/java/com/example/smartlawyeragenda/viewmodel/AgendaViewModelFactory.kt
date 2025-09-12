package com.example.smartlawyeragenda.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smartlawyeragenda.repository.MainRepository
import com.example.smartlawyeragenda.utils.BackupManager

class AgendaViewModelFactory(
    private val repository: MainRepository,
    private val backupManager: BackupManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AgendaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AgendaViewModel(repository, backupManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
