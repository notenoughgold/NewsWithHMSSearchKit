package com.niskender.newswithhmssearchkit.data

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface TokenRequestService {

    @FormUrlEncoded
    @POST("oauth2/v3/token")
    suspend fun getRequestToken(
        @Field("grant_type") grantType: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String
    ): TokenResponse

}