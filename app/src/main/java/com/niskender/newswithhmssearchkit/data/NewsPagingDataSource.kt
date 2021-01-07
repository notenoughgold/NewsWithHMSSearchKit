package com.niskender.newswithhmssearchkit.data

import androidx.paging.PagingSource
import com.huawei.hms.searchkit.bean.NewsItem
import retrofit2.HttpException
import java.io.IOException

class NewsPagingDataSource(private val repository: NewsRepository, private val query: String) :
    PagingSource<Int, NewsItem>()  {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NewsItem> {
        try {
            // Start refresh at page 1 if undefined.
            val nextPageNumber = params.key ?: 1
            return when (val response = repository.getNews(query, nextPageNumber)) {
                is NewsState.Success -> {
                    LoadResult.Page(
                        data = response.data,
                        prevKey = null, // Only paging forward.
                        nextKey = nextPageNumber + 1
                    )
                }
                is NewsState.Error -> LoadResult.Error(response.error)
            }
        } catch (e: IOException) {
            // IOException for network failures.
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            return LoadResult.Error(e)
        }
    }
}