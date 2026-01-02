package com.group22.smartgreenhouse.ui.pages.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.group22.smartgreenhouse.data.api.RetrofitClient
import com.group22.smartgreenhouse.data.repository.AuthRepository

class AccountSettingsViewModelFactory(
    private val ctx: Context        // real Context comes from LocalContext
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val authRepo = AuthRepository(ctx)             // ✔️  pass Context
        @Suppress("UNCHECKED_CAST")
        return AccountSettingsActivityViewModel(authRepo) as T
    }
}
