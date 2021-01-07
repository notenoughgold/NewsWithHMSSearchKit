package com.niskender.newswithhmssearchkit

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.huawei.hms.searchkit.bean.NewsItem
import com.niskender.newswithhmssearchkit.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var listAdapter: NewsAdapter
    private lateinit var imm: InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        listAdapter = NewsAdapter(NewsAdapter.NewsComparator, onItemClicked)
        binding.rv.adapter =
            listAdapter.withLoadStateFooter(NewsLoadStateAdapter(listAdapter))

        binding.swipeRefreshLayout.setOnRefreshListener {
            Log.d(TAG, "SwipeRefreshLayout onRefresh")
            if (viewModel.hasCredentials.value!!) {
                listAdapter.refresh()
            } else {
                viewModel.getRequestToken()
            }
        }

        //observe isRefreshing live data to control refreshing animation
        lifecycleScope.launchWhenResumed {
            viewModel.isRefreshing.observe(this@MainActivity) {
                binding.swipeRefreshLayout.isRefreshing = it
            }
        }
        //set isRefreshing live data to false when list is done loading
        listAdapter.addLoadStateListener {
            if (it.refresh is LoadState.NotLoading) {
                viewModel.setIsRefreshing(false)
            } else if (it.refresh is LoadState.Error) {
                viewModel.setIsRefreshing(false)
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchMenuItem = menu?.findItem(R.id.searchItem)
        val searchView = searchMenuItem?.actionView as SearchView
        searchView.isIconifiedByDefault = false


        searchMenuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                binding.tv.visibility = View.GONE
                item!!.actionView.requestFocus()
                //get input method
                imm.toggleSoftInput(
                    InputMethodManager.SHOW_IMPLICIT,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                imm.hideSoftInputFromWindow(
                    searchView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
                return true
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.trim().length > 3) {
                        viewModel.setIsRefreshing(true)
                        lifecycleScope.launch {
                            viewModel.searchNews(it.trim())
                                .collectLatest { value: PagingData<NewsItem> ->
                                    listAdapter.submitData(value)
                                }
                        }
                        supportActionBar?.title = it.trim()
                        searchMenuItem.collapseActionView()

                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Search term too short",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
        return true
    }

    private val onItemClicked = { it: NewsItem ->
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(it.clickUrl))
    }

    companion object {
        const val TAG = "MainActivity"
    }
}