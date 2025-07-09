package com.wenbo.test.remote

import android.content.Context
import com.wenbo.test.model.Config
import retrofit2.Retrofit
import javax.inject.Inject

class ApiServiceFactory @Inject constructor(
    private val retrofit: Retrofit
) {
    fun create(useMock: Boolean = Config.DEBUG): ApiService {
        return if (useMock) {
            MockApiService()
        } else {
            RetrofitApiService(retrofit)
        }
    }
}