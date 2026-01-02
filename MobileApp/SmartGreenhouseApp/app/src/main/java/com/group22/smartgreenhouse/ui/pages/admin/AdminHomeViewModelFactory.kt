package com.group22.smartgreenhouse.ui.pages.admin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.group22.smartgreenhouse.data.api.RetrofitClient
import com.group22.smartgreenhouse.data.repository.AdminRepository

class AdminHomeViewModelFactory(private val ctx: Context ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val api  = RetrofitClient.getAdminApi(ctx)
        val repo = AdminRepository(api)
        @Suppress("UNCHECKED_CAST")
        return AdminHomeViewModel(repo) as T
    }
}
