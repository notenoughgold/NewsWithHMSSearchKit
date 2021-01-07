package com.niskender.newswithhmssearchkit.data

import com.huawei.hms.searchkit.bean.NewsItem

sealed class NewsState {
    data class Success(val data: List<NewsItem>) : NewsState()
    data class Error(val error: Exception) : NewsState()
}