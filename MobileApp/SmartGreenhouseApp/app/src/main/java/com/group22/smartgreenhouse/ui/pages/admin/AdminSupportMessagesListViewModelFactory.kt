package com.group22.smartgreenhouse.ui.pages.admin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.group22.smartgreenhouse.data.api.RetrofitClient
import com.group22.smartgreenhouse.data.repository.SupportRepository

class SupportMessageViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val api = RetrofitClient.getSupportApi(context)
        val repository = SupportRepository(api)

        return when {
            modelClass.isAssignableFrom(AdminSupportMessagesListViewModel::class.java) -> {
                AdminSupportMessagesListViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AdminSupportMessageDetailViewModel::class.java) -> {
                AdminSupportMessageDetailViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}