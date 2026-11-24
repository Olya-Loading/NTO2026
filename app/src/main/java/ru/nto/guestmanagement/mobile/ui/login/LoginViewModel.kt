package ru.nto.guestmanagement.mobile.ui.login


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.nto.guestmanagement.mobile.data.ApiResult
import ru.nto.guestmanagement.mobile.data.RequestsApi
import ru.nto.guestmanagement.mobile.di.AppContainer

class LoginViewModel(private val appContainer: AppContainer, sessionExpired: Boolean) :
    ViewModel() {
    private val api = RequestsApi(appContainer.httpClient)
    var info by mutableStateOf(UserLogin("", false, false, null, sessionExpired))
    fun changeCode(code: String) {
        info = info.copy(code = code, errorMessage = null, sessionExpired = false)
    }

    val isMatch: Boolean get() = info.code.matches(Regex("^[A-Za-z0-9]{4}\$")) && !info.isLoading

    init {
        viewModelScope.launch {
            val saved = appContainer.session.current()
            if (!saved.isNullOrBlank()) {
                info = info.copy(code = saved, isLogin = true)
            }

        }
    }


    fun onClick(onLoggedIn: () -> Unit) {
        if (!isMatch || info.isLoading) return
        viewModelScope.launch {
            info = info.copy(isLoading = true, errorMessage = null, sessionExpired = false)
            try {
                when (api.auth(info.code)) {
                    is ApiResult.Success -> {
                        appContainer.session.save(info.code)
                        info = info.copy(isLogin = true);
                    }

                    ApiResult.Unauthorized -> {
                        info = info.copy(errorMessage = "Неверный код")
                    }

                    else -> {
                        info = info.copy(errorMessage = "Ошибка входа")
                    }

                }


            } catch (e: Exception) {
                info = info.copy(errorMessage = "Ошибка")
            } finally {
                info = info.copy(isLoading = false)
            }
        }
    }


}