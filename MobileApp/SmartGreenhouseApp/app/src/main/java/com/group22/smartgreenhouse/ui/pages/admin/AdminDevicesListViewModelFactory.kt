package com.group22.smartgreenhouse.ui.pages.admin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.group22.smartgreenhouse.data.api.RetrofitClient
import com.group22.smartgreenhouse.data.repository.AdminRepository
import kotlin.jvm.java

class AdminDevicesListViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminDevicesListViewModel::class.java)) {
            val api = RetrofitClient.getAdminApi(context)
            val repository = AdminRepository(api)
            return AdminDevicesListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}