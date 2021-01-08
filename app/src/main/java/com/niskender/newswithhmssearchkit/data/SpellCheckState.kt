package com.niskender.newswithhmssearchkit.data

import com.huawei.hms.searchkit.bean.SpellCheckResponse

sealed class SpellCheckState {
        data class Success(val data: SpellCheckResponse) : SpellCheckState()
        data class Failure(val exception: Exception) : SpellCheckState()
    }