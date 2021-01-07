package com.niskender.newswithhmssearchkit.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.niskender.newswithhmssearchkit.R
import com.niskender.newswithhmssearchkit.databinding.LayoutNetworkStateItemBinding

class NewsLoadStateAdapter(private val adapter: NewsAdapter)  :
    LoadStateAdapter<NewsLoadStateAdapter.NetworkStateItemViewHolder>() {
    override fun onBindViewHolder(
        holder: NetworkStateItemViewHolder,
        loadState: LoadState
    ) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): NetworkStateItemViewHolder {
        return NetworkStateItemViewHolder(parent) { adapter.retry() }
    }

    class NetworkStateItemViewHolder(
        parent: ViewGroup,
        private val retryCallback: () -> Unit
    ) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_network_state_item, parent, false)
    ) {
        private val binding = LayoutNetworkStateItemBinding.bind(itemView)
        private val retry = binding.retryButton.also {
            it.setOnClickListener {
                retryCallback()
            }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.errorMsg.text = loadState.error.localizedMessage
            }
            binding.progressBar.isVisible = loadState is LoadState.Loading
            retry.isVisible = loadState is LoadState.Error
            binding.errorMsg.isVisible = loadState is LoadState.Error
        }
    }
}
