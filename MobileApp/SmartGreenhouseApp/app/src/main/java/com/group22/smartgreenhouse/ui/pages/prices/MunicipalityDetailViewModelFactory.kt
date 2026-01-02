package com.group22.smartgreenhouse.ui.pages.prices

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.group22.smartgreenhouse.data.api.RetrofitClient
import com.group22.smartgreenhouse.data.repository.MunicipalityRepository
import com.group22.smartgreenhouse.util.SessionManager

class MunicipalityDetailViewModelFactory(
    private val context: Context,
    private val municipalityId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val api = RetrofitClient.getMunicipalityApi(context)
        val sessionManager = SessionManager
        val repository = MunicipalityRepository(api, sessionManager)
        return MunicipalityDetailViewModel(repository, municipalityId) as T
    }
}