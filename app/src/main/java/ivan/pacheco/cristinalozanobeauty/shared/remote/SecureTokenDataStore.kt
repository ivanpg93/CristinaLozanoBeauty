package ivan.pacheco.cristinalozanobeauty.shared.remote

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Manages secure access token storage
 */
object SecureTokenDataStore {

    private const val DATASTORE_NAME = "secure_prefs"
    private val TOKEN_KEY = stringPreferencesKey("access_token")

    // Keystore settings
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "SecureTokenKey"

    // Transformación de cifrado AES/GCM sin padding
    private const val TRANSFORMATION = "AES/GCM/NoPadding"

    // IV size (GCM standard)
    private const val IV_SIZE = 12

    // GCM Authentication Tag Size
    private const val TAG_SIZE = 128

    // Get instance from context
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(DATASTORE_NAME)

    /**
     * Save encrypted token to DataStore
     */
    suspend fun saveToken(context: Context, token: String) {

        // Get or create AES key
        val secretKey = getOrCreateSecretKey()

        // Encrypt token
        val encrypted = encrypt(token, secretKey)

        // Save encrypted token
        context.dataStore.edit { prefs -> prefs[TOKEN_KEY] = encrypted }
    }

    /**
     * Read token from DataStore and decrypts it
     */
    suspend fun readToken(context: Context): String? {
        val secretKey = getOrCreateSecretKey()
        val encrypted = context.dataStore.data.first()[TOKEN_KEY] ?: return null
        return decrypt(encrypted, secretKey)
    }

    /**
     * Obtains AES key from Keystore or creates it if it does not exist
     */
    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

        // If key exits, return
        keyStore.getKey(KEY_ALIAS, null)?.let {
            return it as SecretKey
        }

        // If not exists, create
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setRandomizedEncryptionRequired(true)
            .build()
        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }

    /**
     *
     * Encrypts token using AES/GCM.
     * Android Keystore automatically generates secure IV
     * Keep IV + ciphertext together so can decrypt them later
     */
    private fun encrypt(token: String, secretKey: SecretKey): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        val encryptedBytes = cipher.doFinal(token.toByteArray(Charsets.UTF_8))

        // Get IV automatically generated
        val iv = cipher.iv

        // Store IV + ciphertext together
        val combined = iv + encryptedBytes
        return Base64.encodeToString(combined, Base64.DEFAULT)
    }

    /**
     * Decrypt encrypt token
     * Extract IV from start of byte array before decrypting
     */
    private fun decrypt(encryptedData: String, secretKey: SecretKey): String {
        val combined = Base64.decode(encryptedData, Base64.DEFAULT)

        // Separate IV and ciphertext
        val iv = combined.copyOfRange(0, IV_SIZE)
        val cipherBytes = combined.copyOfRange(IV_SIZE, combined.size)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(TAG_SIZE, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

        val decryptedBytes = cipher.doFinal(cipherBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }

}