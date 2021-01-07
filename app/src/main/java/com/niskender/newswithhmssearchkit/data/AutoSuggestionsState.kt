package com.niskender.newswithhmssearchkit.data

import com.huawei.hms.searchkit.bean.SuggestObject
import java.lang.Exception


sealed class AutoSuggestionsState {
    object Loading : AutoSuggestionsState()
    data class Success(val data: List<SuggestObject>) : AutoSuggestionsState()
    data class Failure(val exception: Exception) : AutoSuggestionsState()
}