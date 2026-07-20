package com.buylog.data.network

import com.buylog.data.model.Product
import com.buylog.platform.MD5
import kotlinx.coroutines.await
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.w3c.fetch.Response
import kotlin.js.Promise
import kotlin.js.json

@Suppress("UNUSED_PARAMETER")
private external fun fetch(url: String, init: dynamic): Promise<Response>

actual class HaodankuApiClient actual constructor() {
    private val appId = "20270127"
    private val appSecret = "fbc2cd0fd3975e9f849f52f8fa16ed54"
    private val apiMethod = "analyze.clipboard"
    private val apiUrl = "https://v3.api.haodanku.com/rest"

    actual suspend fun fetchProductInfo(content: String): Product? {
        return try {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val date = "${now.year}-${
                now.monthNumber.toString().padStart(2, '0')
            }-${now.dayOfMonth.toString().padStart(2, '0')} ${
                now.hour.toString().padStart(2, '0')
            }:${now.minute.toString().padStart(2, '0')}:${now.second.toString().padStart(2, '0')}"
            val params = mapOf(
                "method" to apiMethod,
                "app_id" to appId,
                "date" to date,
                "content" to content
            )
            val sortedKeys = params.keys.sorted()
            val signStr = sortedKeys.joinToString("") { k -> "$k${params[k]}" } + appSecret
            val sign = MD5.hash(signStr)
            val bodyMap = params + ("sign" to sign)
            val body = buildJsonString(bodyMap)
            val response = fetch(
                apiUrl,
                json(
                    "method" to "POST",
                    "headers" to json("Content-Type" to "application/json"),
                    "body" to body
                )
            ).await<Response>()
            val text = response.text().await<String>()
            val jsonObj = kotlinx.serialization.json.Json.parseToJsonElement(text).jsonObject
            if (jsonObj["code"]?.jsonPrimitive?.int == 200) {
                val data = jsonObj["data"]?.jsonObject ?: return null
                Product(
                    title = data["item_title"]?.jsonPrimitive?.content ?: "",
                    imageUrl = data["item_pic"]?.jsonPrimitive?.content ?: "",
                    productUrl = data["item_url"]?.jsonPrimitive?.content ?: "",
                    platform = if (data["plat_type"]?.jsonPrimitive?.int == 1) "淘宝" else "京东",
                    price = data["item_end_price"]?.jsonPrimitive?.content ?: "",
                    images = data["images"]?.jsonPrimitive?.content ?: ""
                )
            } else {
                val msg = jsonObj["msg"]?.jsonPrimitive?.content ?: ""
                val errorMsg = msg.ifBlank { "解析失败，该商品已下架或不在联盟推广计划内！" }
                throw Exception(errorMsg)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    private fun buildJsonString(map: Map<String, String>): String {
        val entries =
            map.entries.joinToString(",") { (k, v) -> "\"$k\":\"${v.replace("\"", "\\\"")}\"" }
        return "{$entries}"
    }
}
