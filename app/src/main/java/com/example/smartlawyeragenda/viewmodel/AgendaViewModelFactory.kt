package com.example.smartlawyeragenda.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smartlawyeragenda.repository.MainRepository
import com.example.smartlawyeragenda.utils.BackupManager

/**
 * Factory for creating AgendaViewModel with required dependencies
 */
class AgendaViewModelFactory(
    private val repository: MainRepository,
    private val backupManager: BackupManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AgendaViewModel::class.java) -> {
                AgendaViewModel(repository, backupManager) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}