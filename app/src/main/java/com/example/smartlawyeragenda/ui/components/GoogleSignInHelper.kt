package com.example.smartlawyeragenda.ui.components

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import org.json.JSONObject

/**
 * Google Sign-In helper using Credential Manager.
 */
class GoogleSignInHelper(private val context: Context) {

    private val credentialManager = CredentialManager.create(context)

    suspend fun signIn(): GoogleSignInResult? {
        return try {
            val serverClientId = resolveServerClientId()
            if (serverClientId.isNullOrBlank()) {
                Log.e("GoogleSignInHelper", "Missing default_web_client_id. Check google-services.json setup.")
                return null
            }

            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(serverClientId)
                .setFilterByAuthorizedAccounts(false)
                .build()

            val credentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                context = context,
                request = credentialRequest
            )

            handleSignInResult(result)
        } catch (e: GetCredentialException) {
            Log.e("GoogleSignInHelper", "Sign-in failed", e)
            null
        } catch (e: Exception) {
            Log.e("GoogleSignInHelper", "Unexpected error during sign-in", e)
            null
        }
    }

    fun signOut(): Result<Unit> {
        return try {
            // Credential Manager has no direct sign-out; app-level session cleanup is handled in ViewModel.
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("GoogleSignInHelper", "Sign-out failed", e)
            Result.failure(e)
        }
    }

    private fun handleSignInResult(result: GetCredentialResponse): GoogleSignInResult? {
        val credential = result.credential
        if (credential !is CustomCredential) {
            Log.e("GoogleSignInHelper", "Unexpected credential type")
            return null
        }

        if (credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            Log.e("GoogleSignInHelper", "Unexpected custom credential type: ${credential.type}")
            return null
        }

        return try {
            val tokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val idToken = tokenCredential.idToken
            val email = extractEmailFromIdToken(idToken)
                ?: tokenCredential.id.takeIf { it.contains("@") }

            if (email.isNullOrBlank()) {
                Log.e("GoogleSignInHelper", "Unable to resolve account email from sign-in token")
                return null
            }

            GoogleSignInResult(
                idToken = idToken,
                displayName = tokenCredential.displayName ?: "Unknown",
                email = email,
                isSuccess = true
            )
        } catch (e: GoogleIdTokenParsingException) {
            Log.e("GoogleSignInHelper", "Invalid Google ID token", e)
            null
        } catch (e: Exception) {
            Log.e("GoogleSignInHelper", "Failed parsing sign-in response", e)
            null
        }
    }

    private fun resolveServerClientId(): String? {
        val id = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
        if (id == 0) return null
        return context.getString(id).trim().takeIf { it.isNotBlank() }
    }

    private fun extractEmailFromIdToken(idToken: String): String? {
        return runCatching {
            val parts = idToken.split('.')
            if (parts.size < 2) return null

            val payload = Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
            val payloadJson = JSONObject(String(payload, Charsets.UTF_8))
            payloadJson.optString("email").takeIf { it.isNotBlank() }
        }.getOrNull()
    }
}

/**
 * Data class representing Google Sign-In result.
 */
data class GoogleSignInResult(
    val idToken: String,
    val displayName: String,
    val email: String,
    val isSuccess: Boolean
)

/**
 * Composable sign-in button.
 */
@Composable
fun GoogleSignInButton(
    onSignInClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    Button(
        onClick = onSignInClick,
        enabled = !isLoading,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = ButtonDefaults.outlinedButtonBorder(enabled = !isLoading).copy(
            brush = SolidColor(MaterialTheme.colorScheme.outline)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.width(20.dp),
                color = MaterialTheme.colorScheme.onSurface,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Signing in...")
        } else {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Google Sign In",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign in with Google")
        }
    }
}
