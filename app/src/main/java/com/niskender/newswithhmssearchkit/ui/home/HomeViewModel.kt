package com.niskender.newswithhmssearchkit.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.huawei.hms.searchkit.bean.NewsItem
import com.niskender.newswithhmssearchkit.data.NewsPagingDataSource
import com.niskender.newswithhmssearchkit.data.NewsRepository
import kotlinx.coroutines.flow.Flow

class HomeViewModel @ViewModelInject constructor(private val repository: NewsRepository) :
    ViewModel() {

    private var lastSearchQuery: String? = null
    var lastFlow: Flow<PagingData<NewsItem>>? = null

    fun searchNews(query: String): Flow<PagingData<NewsItem>> {

        return if (query != lastSearchQuery) {
            lastSearchQuery = query
            lastFlow = Pager(PagingConfig(pageSize = 10)) {
                NewsPagingDataSource(repository, query)
            }.flow.cachedIn(viewModelScope)
            lastFlow as Flow<PagingData<NewsItem>>
        } else {
            lastFlow!!
        }
    }

    companion object {
        const val TAG = "MainViewModel"
    }
}