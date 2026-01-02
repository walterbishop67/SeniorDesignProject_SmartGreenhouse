package com.group22.smartgreenhouse.ui.pages.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.group22.smartgreenhouse.data.api.RetrofitClient
import com.group22.smartgreenhouse.data.api.SupportMessageApi
import com.group22.smartgreenhouse.data.repository.SupportRepository
import kotlin.jvm.java

class UserSupportMessagesListViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserSupportMessagesListViewModel::class.java)) {
            val api        = RetrofitClient.getSupportApi(context)
            val repository = SupportRepository(api)
            @Suppress("UNCHECKED_CAST")
            return UserSupportMessagesListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
