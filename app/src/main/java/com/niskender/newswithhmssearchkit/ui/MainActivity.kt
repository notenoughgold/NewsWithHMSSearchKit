package com.niskender.newswithhmssearchkit.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.niskender.newswithhmssearchkit.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//
//        listAdapter = NewsAdapter(NewsAdapter.NewsComparator, onItemClicked)
//        binding.rv.adapter =
//            listAdapter.withLoadStateFooter(NewsLoadStateAdapter(listAdapter))
//
//        binding.swipeRefreshLayout.setOnRefreshListener {
//            Log.d(TAG, "SwipeRefreshLayout onRefresh")
//            viewModel
//            if (viewModel.hasCredentials.value == true) {
//                listAdapter.refresh()
//            } else {
//                searchTerm?.let { viewModel.getRequestToken { viewModel.searchNews(it) } }
//            }
//        }
        //observe access token situation
//        lifecycleScope.launch {
//            viewModel.hasCredentials.observe(this@MainActivity) {
//
//            }
//        }

//        //observe isRefreshing live data to control refreshing animation
//        lifecycleScope.launch {
//            viewModel.isRefreshing.observe(this@MainActivity) {
//                binding.swipeRefreshLayout.isRefreshing = it
//            }
//        }
//        //set isRefreshing live data to false when list is done loading
//        listAdapter.addLoadStateListener {
//            if (it.refresh is LoadState.NotLoading) {
//                viewModel.setIsRefreshing(false)
//            } else if (it.refresh is LoadState.Error) {
//                viewModel.setIsRefreshing(false)
//                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
//            }
//        }
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_main, menu)
//
//        val searchMenuItem = menu?.findItem(R.id.searchItem)
//        val searchView = searchMenuItem?.actionView as SearchView
//        searchView.isIconifiedByDefault = false
//
//
//        searchMenuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
//            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
//                binding.tv.visibility = View.GONE
//                item!!.actionView.requestFocus()
//                //get input method
//                imm.toggleSoftInput(
//                    InputMethodManager.SHOW_IMPLICIT,
//                    InputMethodManager.HIDE_NOT_ALWAYS
//                )
//                return true
//            }
//
//            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
//                imm.hideSoftInputFromWindow(
//                    searchView.windowToken,
//                    InputMethodManager.HIDE_NOT_ALWAYS
//                )
//                return true
//            }
//        })
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                query?.let {
//                    if (it.trim().length > 3) {
//                        searchTerm = it.trim()
//                        viewModel.setIsRefreshing(true)
//                        lifecycleScope.launch {
//                            viewModel.searchNews(searchTerm!!)
//                                .collectLatest { value: PagingData<NewsItem> ->
//                                    listAdapter.submitData(value)
//                                }
//                        }
//                        supportActionBar?.title = searchTerm
//                        searchMenuItem.collapseActionView()
//
//                    } else {
//                        Toast.makeText(
//                            this@MainActivity,
//                            "Search term too short",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                return true
//            }
//
//        })
//        return true
//    }


    companion object {
        const val TAG = "MainActivity"
    }
}