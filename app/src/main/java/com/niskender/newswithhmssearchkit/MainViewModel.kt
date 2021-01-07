package com.niskender.newswithhmssearchkit

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.huawei.hms.searchkit.SearchKitInstance
import com.huawei.hms.searchkit.bean.NewsItem
import com.niskender.newswithhmssearchkit.data.NewsPagingDataSource
import com.niskender.newswithhmssearchkit.data.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(private val repository: NewsRepository) :
    ViewModel() {

    private val _hasCredentials = MutableLiveData(false)
    val hasCredentials: LiveData<Boolean> = _hasCredentials

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private var accessToken: String? = null

    init {
        getRequestToken()
    }

    fun searchNews(query: String): Flow<PagingData<NewsItem>> {
        return Pager(PagingConfig(pageSize = 10)) {
            NewsPagingDataSource(repository, query)
        }.flow.cachedIn(viewModelScope)
    }

    fun getRequestToken() {
        viewModelScope.launch {
            try {
                accessToken = repository.getRequestToken().access_token
                SearchKitInstance.getInstance()
                    .setInstanceCredential(accessToken)
                SearchKitInstance.instance.newsSearcher.setTimeOut(5000)
                Log.d(
                    TAG,
                    "SearchKitInstance.instance.setInstanceCredential done $accessToken"
                )
                _hasCredentials.postValue(true)

            } catch (e: Exception) {
                Log.e(TAG, "get token error", e)
            }
        }
    }

    fun setIsRefreshing(bool: Boolean) {
        _isRefreshing.postValue(bool)
    }

    companion object {
        const val TAG = "MainViewModel"
    }
}