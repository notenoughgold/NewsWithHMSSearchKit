package com.niskender.newswithhmssearchkit.data

import android.util.Log
import com.huawei.hms.searchkit.SearchKitInstance
import com.huawei.hms.searchkit.bean.CommonSearchRequest
import com.huawei.hms.searchkit.utils.Language
import com.huawei.hms.searchkit.utils.Region
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NewsRepository(
    private val tokenRequestService: TokenRequestService
) {
    suspend fun getRequestToken() = tokenRequestService.getRequestToken(
        "client_credentials",
        "103633039",
        "7e8b117a8f50be029a74042505e25f5971700ad72c0c72eb35b51c4aef462a1a"
    )

    suspend fun getNews(query: String, pageNumber: Int): NewsState = withContext(Dispatchers.IO) {

        var newsState: NewsState

        Log.i(TAG, "getting news $query $pageNumber")
        val commonSearchRequest = CommonSearchRequest()
        commonSearchRequest.setQ(query)
        commonSearchRequest.setLang(Language.ENGLISH)
        commonSearchRequest.setSregion(Region.WHOLEWORLD)
        commonSearchRequest.setPs(10)
        commonSearchRequest.setPn(pageNumber)
        try {
            val result = SearchKitInstance.instance.newsSearcher.search(commonSearchRequest)
            newsState = if (result != null) {
                if (result.data.size > 0) {
                    Log.i(TAG, "got news ${result.data.size}")
                    NewsState.Success(result.data)
                } else {
                    NewsState.Error(Exception("no more news"))
                }
            } else {
                NewsState.Error(Exception("fetch news error"))
            }

        } catch (e: Exception) {
            newsState = NewsState.Error(e)
            Log.e(TAG, "caught news search exception", e)
        }

        return@withContext newsState

    }

    companion object {
        const val TAG = "NewsRepository"
    }
}