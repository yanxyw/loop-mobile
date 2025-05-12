package com.loop.mobile.domain.utils

import com.loop.mobile.domain.entities.DecodedUser
import io.ktor.util.decodeBase64String
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

object JwtUtils {
    fun decodePayload(token: String): JsonObject? {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return null
            val decoded = parts[1].decodeBase64String()
            Json.parseToJsonElement(decoded).jsonObject
        } catch (e: Exception) {
            null
        }
    }

    fun decodeUser(token: String): DecodedUser? {
        val payload = decodePayload(token) ?: return null
        return DecodedUser(
            userId = payload["sub"]?.jsonPrimitive?.longOrNull ?: return null,
            email = payload["email"]?.jsonPrimitive?.contentOrNull ?: return null,
            username = payload["username"]?.jsonPrimitive?.contentOrNull ?: return null,
            isAdmin = payload["isAdmin"]?.jsonPrimitive?.booleanOrNull ?: false,
            profileUrl = payload["profileUrl"]?.jsonPrimitive?.contentOrNull
        )
    }
    
    fun isTokenExpired(token: String): Boolean {
        val payload = decodePayload(token) ?: return true // If no payload, consider it expired
        val exp = payload["exp"]?.jsonPrimitive?.longOrNull
            ?: return true // If no 'exp' field, assume expired

        // Compare the expiration time with the current time (in seconds)
        return getCurrentTimeInSeconds() > exp
    }
}

expect fun getCurrentTimeInSeconds(): Long
