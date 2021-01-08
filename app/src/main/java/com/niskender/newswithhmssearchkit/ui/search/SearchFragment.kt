package com.niskender.newswithhmssearchkit.ui.search

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.niskender.newswithhmssearchkit.R
import com.niskender.newswithhmssearchkit.data.AutoSuggestionsState
import com.niskender.newswithhmssearchkit.data.SpellCheckState
import com.niskender.newswithhmssearchkit.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()

    private lateinit var imm: InputMethodManager
    private var searchView: SearchView? = null

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        setHasOptionsMenu(true)

        imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        lifecycleScope.launch {
            viewModel.searchQuery.debounce(500).filter { s: String ->
                return@filter s.length > 3
            }.distinctUntilChanged().flatMapLatest { query ->
                Log.d(TAG, "getting suggestions for term: $query")
                viewModel.getSuggestions(query).catch {
                }
            }.flowOn(Dispatchers.Default).collect {
                if (it is AutoSuggestionsState.Success) {
                    val list = it.data
                    Log.d(TAG, "${list.size} suggestion")
                    binding.chipGroup.removeAllViews()
                    list.forEach { suggestion ->
                        val chip = Chip(requireContext())
                        chip.text = suggestion.name
                        chip.isClickable = true
                        chip.setOnClickListener { thisChip ->
                            imm.hideSoftInputFromWindow(
                                thisChip.windowToken,
                                InputMethodManager.HIDE_NOT_ALWAYS
                            )
                            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                                "query",
                                suggestion.name
                            )
                            findNavController().popBackStack()
                        }
                        binding.chipGroup.addView(chip)
                    }
                } else if (it is AutoSuggestionsState.Failure) {
                    Log.e(TAG, "suggestions request error", it.exception)
                }

            }
        }

        lifecycleScope.launch {
            viewModel.searchQuery.debounce(500).filter { s: String ->
                return@filter s.length > 3
            }.distinctUntilChanged().flatMapLatest { query ->
                Log.d(TAG, "spellcheck for term: $query")
                viewModel.getSpellCheck(query).catch {
                    Log.e(TAG, "spellcheck request error", it)
                }
            }.flowOn(Dispatchers.Default).collect {
                if (it is SpellCheckState.Success) {
                    val spellCheckResponse = it.data
                    val correctedStr = spellCheckResponse.correctedQuery
                    val confidence = spellCheckResponse.confidence
                    Log.d(
                        TAG,
                        "corrected query $correctedStr confidence level $confidence"
                    )
                    if (confidence > 0) {
                        binding.tvDidYouMeanToSearch.visibility = View.VISIBLE
                        binding.tvCorrected.visibility = View.VISIBLE
                        binding.tvCorrected.text = correctedStr
                    } else {
                        binding.tvDidYouMeanToSearch.visibility = View.GONE
                        binding.tvCorrected.visibility = View.GONE
                    }

                } else if (it is SpellCheckState.Failure) {
                    Log.e(TAG, "spellcheck request error", it.exception)
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search, menu)
        val searchMenuItem = menu.findItem(R.id.searchItem)
        searchView = searchMenuItem.actionView as SearchView
        searchView?.setIconifiedByDefault(false)
        searchMenuItem.expandActionView()
        searchMenuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                findNavController().previousBackStackEntry?.savedStateHandle?.set("query", "")
                findNavController().popBackStack()
                return true
            }
        })
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                imm.hideSoftInputFromWindow(
                    searchView?.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
                findNavController().previousBackStackEntry?.savedStateHandle?.set("query", query)
                findNavController().popBackStack()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.emitNewTextToSearchQueryFlow(newText ?: "")
                return true
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "SearchFragment"
    }
}