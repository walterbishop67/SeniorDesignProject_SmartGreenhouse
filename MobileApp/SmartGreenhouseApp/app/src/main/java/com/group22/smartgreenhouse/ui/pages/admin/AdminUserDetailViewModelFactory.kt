package com.group22.smartgreenhouse.ui.pages.admin


import AdminUserListViewModel
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.group22.smartgreenhouse.data.api.AdminApi
import com.group22.smartgreenhouse.data.api.RetrofitClient
import com.group22.smartgreenhouse.data.repository.AdminRepository
import kotlin.jvm.java

// UserDetailViewModelFactory.kt
class UserDetailViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserDetailViewModel::class.java)) {
            val adminApi = RetrofitClient.getAdminApi(context)
            return UserDetailViewModel(adminApi) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}