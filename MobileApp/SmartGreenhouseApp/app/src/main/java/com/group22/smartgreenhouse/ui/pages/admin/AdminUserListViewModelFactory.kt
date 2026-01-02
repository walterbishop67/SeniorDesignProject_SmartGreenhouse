package com.group22.smartgreenhouse.ui.pages.admin

import AdminUserListViewModel
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.group22.smartgreenhouse.data.api.RetrofitClient
import com.group22.smartgreenhouse.data.repository.AdminRepository

class AdminUserListViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val api = RetrofitClient.getAdminApi(context)
        val repository = AdminRepository(api)
        return AdminUserListViewModel(repository) as T
    }
}