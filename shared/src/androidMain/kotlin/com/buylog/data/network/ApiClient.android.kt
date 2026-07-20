package com.buylog.data.network

import com.buylog.data.model.Product
import com.buylog.platform.MD5
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

actual class HaodankuApiClient actual constructor() {
    private val client = OkHttpClient()
    private val appId = "20270127"
    private val appSecret = "fbc2cd0fd3975e9f849f52f8fa16ed54"
    private val apiMethod = "analyze.clipboard"
    private val apiUrl = "https://v3.api.haodanku.com/rest"

    actual suspend fun fetchProductInfo(content: String): Product? = withContext(Dispatchers.IO) {
        try {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val date = "${now.year}-${now.monthNumber.toString().padStart(2, '0')}-${now.dayOfMonth.toString().padStart(2, '0')} ${now.hour.toString().padStart(2, '0')}:${now.minute.toString().padStart(2, '0')}:${now.second.toString().padStart(2, '0')}"

            val params = linkedMapOf(
                "method" to apiMethod,
                "app_id" to appId,
                "date" to date,
                "content" to content
            )
            val sign = MD5.hash(params.toSortedMap().entries.joinToString("") { it.key + it.value } + appSecret)

            val jsonBody = JSONObject()
            params.forEach { (k, v) -> jsonBody.put(k, v) }
            jsonBody.put("sign", sign)

            val body = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder().url(apiUrl).post(body).build()

            client.newCall(request).execute().use { response ->
                val result = response.body?.string()
                val jsonRes = JSONObject(result ?: "")
                if (jsonRes.optInt("code") == 200) {
                    val data = jsonRes.getJSONObject("data")
                    val images = data.optString("images")
                    val product = Product(
                        title = data.optString("item_title"),
                        imageUrl = data.optString("item_pic"),
                        productUrl = data.optString("item_url"),
                        platform = if (data.optInt("plat_type") == 1) "淘宝" else "京东",
                        price = data.optString("item_end_price"),
                        images = images
                    )
                    println("DEBUG: fetchProductInfo product = $product")
                    product
                } else {
                    val msg = jsonRes.optString("msg")
                    val errorMsg = msg.ifBlank { "解析失败，该商品已下架或不在联盟推广计划内！" }
                    println("DEBUG: fetchProductInfo failed, code = ${jsonRes.optInt("code")}, msg = $errorMsg")
                    throw Exception(errorMsg)
                }
            }
        } catch (e: Exception) {
            println("DEBUG: fetchProductInfo exception: ${e.message}")
            throw e
        }
    }
}
