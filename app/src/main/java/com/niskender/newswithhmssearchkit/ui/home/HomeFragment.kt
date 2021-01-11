package com.niskender.newswithhmssearchkit.ui.home

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.huawei.hms.searchkit.bean.NewsItem
import com.niskender.newswithhmssearchkit.R
import com.niskender.newswithhmssearchkit.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var listAdapter: NewsAdapter

    private var startedLoading = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        setHasOptionsMenu(true)

        listAdapter = NewsAdapter(NewsAdapter.NewsComparator, onItemClicked)
        binding.rv.adapter =
            listAdapter.withLoadStateFooter(NewsLoadStateAdapter(listAdapter))

        //if user swipe down to refresh, refresh paging adapter
        binding.swipeRefreshLayout.setOnRefreshListener {
            listAdapter.refresh()
        }

        // Listen to search term returned from Search Fragment
        setFragmentResultListener("requestKey") { _, bundle ->
            // We use a String here, but any type that can be put in a Bundle is supported
            val result = bundle.getString("bundleKey")
            binding.tv.visibility = View.GONE
            if (result != null) {
                binding.toolbar.subtitle = "News about $result"
                lifecycleScope.launchWhenResumed {
                    binding.swipeRefreshLayout.isRefreshing = true
                    viewModel.searchNews(result).collectLatest { value: PagingData<NewsItem> ->
                        listAdapter.submitData(value)
                    }
                }
            }
        }

        //need to listen to paging adapter load state to stop swipe to refresh layout animation
        //if load state contain error, show a toast.
        listAdapter.addLoadStateListener {
            if (it.refresh is LoadState.NotLoading && startedLoading) {
                binding.swipeRefreshLayout.isRefreshing = false
            } else if (it.refresh is LoadState.Error && startedLoading) {
                binding.swipeRefreshLayout.isRefreshing = false
                val loadState = it.refresh as LoadState.Error
                val errorMsg = loadState.error.localizedMessage
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
            } else if (it.refresh is LoadState.Loading) {
                startedLoading = true
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.searchItem -> {
                //launch search fragment when search item clicked
                findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        }
    }

    //callback function to be passed to paging adapter, used to launch news links.
    private val onItemClicked = { it: NewsItem ->
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(requireContext(), Uri.parse(it.clickUrl))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "HomeFragment"
    }
}