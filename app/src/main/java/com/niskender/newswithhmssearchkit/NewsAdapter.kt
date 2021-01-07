package com.niskender.newswithhmssearchkit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.huawei.hms.searchkit.bean.NewsItem
import com.niskender.newswithhmssearchkit.databinding.LayoutNewsItemBinding
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class NewsAdapter(
    diffCallback: DiffUtil.ItemCallback<NewsItem>,
    private val callback: (newsItem: NewsItem) -> Unit
) : PagingDataAdapter<NewsItem, NewsAdapter.NewsViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(
            LayoutNewsItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), callback
        )
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item!!)
    }

    inner class NewsViewHolder(
        private val binding: LayoutNewsItemBinding,
        private val callback: (news: NewsItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(newsItem: NewsItem) {
            binding.ivNewsTitle.text = newsItem.title
            val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
            val date =
                Instant.ofEpochSecond(newsItem.publishTime.toLong()).atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
            val timeStr = formatter.format(date)
            binding.tvPublishTime.text = timeStr
            binding.tvPublisher.text = newsItem.provider.site_name
            binding.root.setOnClickListener {
                callback(newsItem)
            }
        }
    }

    object NewsComparator :
        DiffUtil.ItemCallback<NewsItem>() {

        override fun areItemsTheSame(oldItem: NewsItem, newItem: NewsItem): Boolean {
            return oldItem.clickUrl == newItem.clickUrl
        }

        override fun areContentsTheSame(oldItem: NewsItem, newItem: NewsItem): Boolean {
            return oldItem.clickUrl == newItem.clickUrl
        }
    }
}