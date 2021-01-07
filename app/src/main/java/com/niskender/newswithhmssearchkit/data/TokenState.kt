package com.niskender.newswithhmssearchkit.data

import java.lang.Exception

sealed class TokenState {
    object Loading : TokenState()
    data class Success(val data: String) : TokenState()
    data class Failure(val exception: Exception) : TokenState()
}