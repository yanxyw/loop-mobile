package com.loop.mobile.data.local

import android.content.Context
import android.content.SharedPreferences
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import android.util.Base64
import androidx.core.content.edit

private const val KEY_ALIAS = "auth_session_key"
private const val ANDROID_KEYSTORE = "AndroidKeyStore"
private const val AES_MODE = "AES/GCM/NoPadding"
private const val PREF_NAME = "secure_auth_prefs"

class SessionStorageImpl(context: Context) : SessionStorage {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        val existingKey = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        if (existingKey != null) return existingKey.secretKey

        val keyGenerator = KeyGenerator.getInstance("AES", ANDROID_KEYSTORE)
        keyGenerator.init(android.security.keystore.KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            android.security.keystore.KeyProperties.PURPOSE_ENCRYPT or android.security.keystore.KeyProperties.PURPOSE_DECRYPT
        ).run {
            setBlockModes(android.security.keystore.KeyProperties.BLOCK_MODE_GCM)
            setEncryptionPaddings(android.security.keystore.KeyProperties.ENCRYPTION_PADDING_NONE)
            build()
        })

        return keyGenerator.generateKey()
    }

    private fun encrypt(value: String): Pair<String, String> {
        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
        val iv = cipher.iv
        val encrypted = cipher.doFinal(value.toByteArray(Charsets.UTF_8))

        val encryptedBase64 = Base64.encodeToString(encrypted, Base64.DEFAULT)
        val ivBase64 = Base64.encodeToString(iv, Base64.DEFAULT)
        return encryptedBase64 to ivBase64
    }

    private fun decrypt(encrypted: String?, iv: String?): String? {
        if (encrypted == null || iv == null) return null
        val cipher = Cipher.getInstance(AES_MODE)
        val spec = GCMParameterSpec(128, Base64.decode(iv, Base64.DEFAULT))
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), spec)
        val decryptedBytes = cipher.doFinal(Base64.decode(encrypted, Base64.DEFAULT))
        return String(decryptedBytes, Charsets.UTF_8)
    }

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        val (accessEncrypted, accessIv) = encrypt(accessToken)
        val (refreshEncrypted, refreshIv) = encrypt(refreshToken)
        prefs.edit {
            putString("accessToken", accessEncrypted)
                .putString("accessTokenIv", accessIv)
                .putString("refreshToken", refreshEncrypted)
                .putString("refreshTokenIv", refreshIv)
        }
    }

    override suspend fun getAccessToken(): String? =
        decrypt(prefs.getString("accessToken", null), prefs.getString("accessTokenIv", null))

    override suspend fun getRefreshToken(): String? =
        decrypt(prefs.getString("refreshToken", null), prefs.getString("refreshTokenIv", null))

    override suspend fun saveAuthProvider(provider: String) {
        val (encrypted, iv) = encrypt(provider)
        prefs.edit {
            putString("authProvider", encrypted)
            putString("authProviderIv", iv)
        }


    }

    override suspend fun getAuthProvider(): String? =
        decrypt(
            prefs.getString("authProvider", null),
            prefs.getString("authProviderIv", null)
        )

    override suspend fun clearSession() {
        prefs.edit { clear() }
    }
}