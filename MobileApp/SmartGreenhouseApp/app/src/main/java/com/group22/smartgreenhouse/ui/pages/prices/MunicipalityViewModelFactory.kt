// AdminMunicipalityListViewModelFactory.kt
package com.group22.smartgreenhouse.ui.pages.admin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.group22.smartgreenhouse.data.api.RetrofitClient
import com.group22.smartgreenhouse.data.repository.MunicipalityRepository
import com.group22.smartgreenhouse.util.SessionManager

class MunicipalityListViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val api = RetrofitClient.getMunicipalityApi(context)
        val sessionManager = SessionManager
        val repository = MunicipalityRepository(api, sessionManager)
        return MunicipalityListViewModel(repository) as T
    }
}