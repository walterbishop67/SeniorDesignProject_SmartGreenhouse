package com.group22.smartgreenhouse.ui.pages.settings

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group22.smartgreenhouse.data.model.SupportMessageRequest
import com.group22.smartgreenhouse.data.repository.SupportRepository
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import androidx.compose.runtime.State
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SupportActivityViewModel(
    private val repository: SupportRepository
) : ViewModel() {

    /*  internal mutable state  */
    private val _state = mutableStateOf<SendState>(SendState.Idle)

    /*  UI reads this immutable wrapper  */
    val state: State<SendState> get() = _state

    fun sendMessage(token: String, subject: String, message: String) {
        viewModelScope.launch {
            _state.value = SendState.Sending

            val req = SupportMessageRequest(
                subject        = subject,
                messageContent = message,
                sentAt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            .format(Date())
            )

            val result = repository.sendMessage(token, req)
            _state.value = if (result.isSuccess) {
                SendState.Success(result.getOrNull() ?: -1)
            } else {
                SendState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    /* -------------------- UI states -------------------- */
    sealed class SendState {
        object Idle               : SendState()
        object Sending            : SendState()
        data class Success(val id: Int) : SendState()
        data class Error  (val message: String) : SendState()
    }
}

