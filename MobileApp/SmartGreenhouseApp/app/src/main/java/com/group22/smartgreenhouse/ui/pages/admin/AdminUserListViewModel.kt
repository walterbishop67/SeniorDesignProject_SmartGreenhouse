// AdminUserListViewModel.kt
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group22.smartgreenhouse.data.model.User
import com.group22.smartgreenhouse.data.repository.AdminRepository
import kotlinx.coroutines.launch
import kotlin.collections.plus
import kotlin.fold

class AdminUserListViewModel(
    private val repository: AdminRepository
) : ViewModel() {

    private val _userList = mutableStateOf<List<User>>(emptyList())
    val userList: List<User> get() = _userList.value

    private val _isLoading = mutableStateOf(false)
    val isLoading: Boolean get() = _isLoading.value

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: String? get() = _errorMessage.value

    private var currentPage = 1
    private var totalPages = 1

    init {
        loadUsers()
    }

    fun loadUsers(page: Int = 1) {
        if (page > totalPages || isLoading) return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.fetchAllUsers(page).fold(
                onSuccess = { response ->
                    currentPage = response.currentPage
                    totalPages = response.totalPages
                    _userList.value = if (page == 1) {
                        response.users
                    } else {
                        _userList.value + response.users
                    }
                },
                onFailure = { error ->
                    _errorMessage.value = error.message ?: "Failed to load users"
                }
            )
            _isLoading.value = false
        }
    }

    fun loadNextPage() {
        if (currentPage < totalPages) {
            loadUsers(currentPage + 1)
        }
    }

    fun dismissError() {
        _errorMessage.value = null
    }
}