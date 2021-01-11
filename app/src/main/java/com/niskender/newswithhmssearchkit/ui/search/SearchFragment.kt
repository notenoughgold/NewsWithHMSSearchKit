package com.niskender.newswithhmssearchkit.ui.search

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        setHasOptionsMenu(true)

        //listen to the change in query text, trigger getSuggestions function after debouncing and filtering
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
                    //create a chip for each suggestion and add them to chip group
                    list.forEach { suggestion ->
                        val chip = Chip(requireContext())
                        chip.text = suggestion.name
                        chip.isClickable = true
                        chip.setOnClickListener {
                            //set fragment result to return search term to home fragment.
                            setFragmentResult(
                                "requestKey",
                                bundleOf("bundleKey" to suggestion.name)
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

        //listen to the change in query text, trigger spellcheck function after debouncing and filtering
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
                        //show spellcheck layout, and set on click listener to send corrected term to home fragment
                        //to be searched
                        binding.tvDidYouMeanToSearch.visibility = View.VISIBLE
                        binding.tvCorrected.visibility = View.VISIBLE
                        binding.tvCorrected.text = correctedStr
                        binding.llSpellcheck.setOnClickListener {
                            setFragmentResult(
                                "requestKey",
                                bundleOf("bundleKey" to correctedStr)
                            )
                            findNavController().popBackStack()
                        }
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
        val searchView = searchMenuItem.actionView as SearchView
        searchView.setIconifiedByDefault(false)
        searchMenuItem.expandActionView()
        searchMenuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                findNavController().popBackStack()
                return true
            }
        })
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return if (query != null && query.length > 3) {
                    setFragmentResult("requestKey", bundleOf("bundleKey" to query))
                    findNavController().popBackStack()
                    true
                } else {
                    Toast.makeText(requireContext(), "Search term is too short", Toast.LENGTH_SHORT)
                        .show()
                    true
                }
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