package com.niskender.newswithhmssearchkit.ui.splash

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huawei.hms.searchkit.SearchKitInstance
import com.niskender.newswithhmssearchkit.data.NewsRepository
import com.niskender.newswithhmssearchkit.data.TokenState
import com.niskender.newswithhmssearchkit.ui.home.HomeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SplashViewModel @ViewModelInject constructor(private val repository: NewsRepository) :
    ViewModel() {

    private var _accessToken = MutableStateFlow<TokenState>(TokenState.Loading)
    var accessToken: StateFlow<TokenState> = _accessToken

    init {
        getRequestToken()
    }

    private fun getRequestToken() {
        viewModelScope.launch {
            try {
                val token = repository.getRequestToken().access_token
                SearchKitInstance.getInstance()
                    .setInstanceCredential(token)
                SearchKitInstance.instance.newsSearcher.setTimeOut(5000)
                Log.d(
                    TAG,
                    "SearchKitInstance.instance.setInstanceCredential done $token"
                )
                _accessToken.emit(TokenState.Success(token))
            } catch (e: Exception) {
                Log.e(HomeViewModel.TAG, "get token error", e)
                _accessToken.emit(TokenState.Failure(e))
            }
        }
    }

    companion object {
        const val TAG = "SplashViewModel"
    }
}