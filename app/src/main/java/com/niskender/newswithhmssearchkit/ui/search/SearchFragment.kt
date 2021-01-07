package com.niskender.newswithhmssearchkit.ui.search

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
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
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.niskender.newswithhmssearchkit.R
import com.niskender.newswithhmssearchkit.data.AutoSuggestionsState
import com.niskender.newswithhmssearchkit.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()

    private lateinit var imm: InputMethodManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        setHasOptionsMenu(true)

        imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        lifecycleScope.launch {
            viewModel.autoSuggestions.collect {
                if (it is AutoSuggestionsState.Success) {
                    val list = it.data
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
                    binding.chipGroup
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
                findNavController().previousBackStackEntry?.savedStateHandle?.set("query", "")
                findNavController().popBackStack()
                return true
            }
        })
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                imm.hideSoftInputFromWindow(
                    searchView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
                findNavController().previousBackStackEntry?.savedStateHandle?.set("query", query)
                findNavController().popBackStack()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.length > 3) {
                        viewModel.getSuggestions(it)
                    }
                }
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