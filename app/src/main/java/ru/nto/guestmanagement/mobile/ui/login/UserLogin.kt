package ru.nto.guestmanagement.mobile.ui.login

data class UserLogin(
    val code: String,
    val isLoading: Boolean,
    val isLogin: Boolean,
    val errorMessage: String? = null,
    val sessionExpired: Boolean
)


