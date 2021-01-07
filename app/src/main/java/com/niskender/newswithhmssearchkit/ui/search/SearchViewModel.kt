package com.niskender.newswithhmssearchkit.ui.search

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niskender.newswithhmssearchkit.data.AutoSuggestionsState
import com.niskender.newswithhmssearchkit.data.NewsRepository
import com.niskender.newswithhmssearchkit.data.TokenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel @ViewModelInject constructor(private val repository: NewsRepository) :
    ViewModel() {

    private var _autoSuggestions =
        MutableStateFlow<AutoSuggestionsState>(AutoSuggestionsState.Loading)
    var autoSuggestions: StateFlow<AutoSuggestionsState> = _autoSuggestions

    fun getSuggestions(str: String) {
        viewModelScope.launch {
            try {
                val suggestions = repository.getAutoSuggestions(str).suggestions
                _autoSuggestions.emit(AutoSuggestionsState.Success(suggestions))
            } catch (e: Exception) {

            }

        }
    }

}
