package com.drugstracking.helpers

import com.drugstracking.entities.SerializablePerson
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.client.HttpClient
import io.ktor.client.features.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import java.lang.RuntimeException
import java.nio.charset.Charset

class DrugsServerHelper(val host: String, val port: String) {

    private val client = HttpClient() {
        install(HttpTimeout) {
            requestTimeoutMillis = 3000
            socketTimeoutMillis = 3000
            connectTimeoutMillis = 3000
        }
    }

    suspend fun saveSettings(people: List<SerializablePerson>) {
        if (host.isEmpty() || port.isEmpty()) {
            throw RuntimeException("Host and Port must be specified!")
        }
        val gson = Gson()
        val data = client.post<HttpResponse> {
            url("http://$host:$port/api/v1/drugs/settings")
            contentType(ContentType.Application.Json)
            body = gson.toJson(people)
        }
        if (data.status != HttpStatusCode.OK) {
            throw RuntimeException("Cant send settings to the server")
        }
    }

    suspend fun loadSettings(): List<SerializablePerson> {
        if (host.isEmpty() || port.isEmpty()) {
            throw RuntimeException("Host and Port must be specified!")
        }
        val data = client.get<HttpResponse>("http://$host:$port/api/v1/drugs/settings")
        if (data.status != HttpStatusCode.OK) {
            throw RuntimeException("Cant get settings from the server")
        }
        val json = data.readText(Charset.defaultCharset())
        return Gson().fromJson(json, object : TypeToken<List<SerializablePerson>>() {}.type)
    }

    suspend fun getPendingDrugs(): List<SerializablePerson> {
        if (host.isEmpty() || port.isEmpty()) {
            throw RuntimeException("Host and Port must be specified!")
        }
        val data = client.get<HttpResponse>("http://$host:$port/api/v1/drugs")
        if (data.status != HttpStatusCode.OK) {
            throw RuntimeException("Cant get a list of pending drugs")
        }
        val json = data.readText(Charset.defaultCharset())
        return Gson().fromJson(json, object : TypeToken<List<SerializablePerson>>() {}.type)
    }

    suspend fun changePendingStatus(personName: String, drugName: String) {
        if (host.isEmpty() || port.isEmpty()) {
            throw RuntimeException("Host and Port must be specified!")
        }

        val data =
            client.put<HttpResponse>("http://$host:$port/api/v1/drugs/$personName/$drugName")
        if (data.status != HttpStatusCode.OK) {
            throw RuntimeException("Cant change status")
        }
    }
}