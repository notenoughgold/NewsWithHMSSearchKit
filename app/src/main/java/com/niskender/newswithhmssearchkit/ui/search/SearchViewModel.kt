package com.niskender.newswithhmssearchkit.ui.search

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niskender.newswithhmssearchkit.data.AutoSuggestionsState
import com.niskender.newswithhmssearchkit.data.NewsRepository
import com.niskender.newswithhmssearchkit.data.SpellCheckState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class SearchViewModel @ViewModelInject constructor(private val repository: NewsRepository) :
    ViewModel() {

    private var _searchQuery = MutableStateFlow<String>("")
    var searchQuery: StateFlow<String> = _searchQuery

    fun getSuggestions(str: String): Flow<AutoSuggestionsState> {
        return flow {
            try {
                val result = repository.getAutoSuggestions(str)
                emit(result)
            } catch (e: Exception) {
            }
        }
    }

    fun getSpellCheck(str: String): Flow<SpellCheckState> {
        return flow {
            try {
                val result = repository.getSpellCheck(str)
                emit(result)
            } catch (e: Exception) {
            }
        }
    }


    fun emitNewTextToSearchQueryFlow(str: String) {
        viewModelScope.launch {
            _searchQuery.emit(str)
        }
    }

}
