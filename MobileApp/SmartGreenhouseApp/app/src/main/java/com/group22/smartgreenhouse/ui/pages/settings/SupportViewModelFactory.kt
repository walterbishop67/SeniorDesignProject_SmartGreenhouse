package com.group22.smartgreenhouse.ui.pages.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.group22.smartgreenhouse.data.api.RetrofitClient
import com.group22.smartgreenhouse.data.repository.SupportRepository

class SupportViewModelFactory(
    private val token: String,
    private val context: Context            // to build Retrofit client, etc.
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val api        = RetrofitClient.getSupportApi(context)
        val repository = SupportRepository(api)
        @Suppress("UNCHECKED_CAST")
        return SupportActivityViewModel(repository) as T
    }
}
