package com.pma.bcc.net

import com.google.gson.GsonBuilder
import com.pma.bcc.model.AppProperties
import com.pma.bcc.model.Temperature
import com.pma.bcc.model.TemperatureTypeAdapter
import mu.KLogging
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.MalformedURLException
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerApiFactoryImpl : ServerApiFactory {
    companion object : KLogging()

    private var serverApi: ServerApi? = null
    private var appProperties: AppProperties

    @Inject constructor (appProperties: AppProperties) : super() {
        this.appProperties = appProperties
    }

    override fun create(): ServerApi {
        if (serverApi == null) {
            val baseUrl = createBaseUrl()
            serverApi = createRetrofitInstance(baseUrl)
        }
        return serverApi!!
    }

    private fun createRetrofitInstance(baseUrl: URL): ServerApi {
        logger.error("createRetrofitInstance url: $baseUrl")
        try {
            val gson = GsonBuilder()
                .registerTypeAdapter(Temperature::class.java, TemperatureTypeAdapter())
                .create()
            return Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(baseUrl)
                .build().create(ServerApi::class.java)
        } catch (e: Exception) {
            logger.error("createRetrofitInstance: $e")
            throw ServerApiFactory.InvalidConnectionSettingsException()
        }
    }

    private fun createBaseUrl(): URL {
        try {
            val url = URL(
                "http",
                appProperties.serverAddress,
                appProperties.serverPort,
                appProperties.apiBaseUrl
            )
            logger.info("createBaseUrl: $url")
            return url
        } catch (e: Exception) {
            logger.error("createBaseUrl: $e")
            throw ServerApiFactory.InvalidConnectionSettingsException()
        }
    }

    override fun invalidate() {
        serverApi = null
    }
}
