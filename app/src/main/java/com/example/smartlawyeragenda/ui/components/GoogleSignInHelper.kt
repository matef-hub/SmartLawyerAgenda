package com.example.smartlawyeragenda.ui.components

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
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

/**
 * Helper class for Google Sign-In functionality using Credential Manager API
 */
class GoogleSignInHelper(private val context: Context) {

    private val credentialManager = CredentialManager.create(context)
    
    // TODO: Replace with actual server client ID from google-services.json
    private val serverClientId = "YOUR_SERVER_CLIENT_ID"

    /**
     * Check if user is already signed in
     */
    suspend fun isSignedIn(intent: Intent?): Boolean {
        return try {
            // For Credential Manager API, we check if we can get a credential
            // This is a simplified check - in a real app, you might want to store
            // the credential state in SharedPreferences or similar
            val credential = getCurrentUser(intent)
            credential != null
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Start the sign-in process using Credential Manager API
     */
    suspend fun signIn(): GoogleSignInResult? {
        return try {
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

    /**
     * Handle the sign-in result from Credential Manager
     */
    private fun handleSignInResult(result: GetCredentialResponse): GoogleSignInResult? {
        val credential = result.credential
        return when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        GoogleSignInResult(
                            idToken = googleIdTokenCredential.idToken,
                            displayName = googleIdTokenCredential.displayName ?: "Unknown",
                            email = googleIdTokenCredential.id,
                            isSuccess = true
                        )
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("GoogleSignInHelper", "Invalid Google ID token", e)
                        null
                    }
                } else {
                    Log.e("GoogleSignInHelper", "Unexpected credential type")
                    null
                }
            }
            else -> {
                Log.e("GoogleSignInHelper", "Unexpected credential type")
                null
            }
        }
    }

    /**
     * Sign out the current user
     * Note: Credential Manager API doesn't provide a direct sign-out method.
     * In a real app, you would clear stored credentials and session information.
     */
    fun signOut(): Result<Unit> {
        return try {
            // For Credential Manager API, sign-out typically involves:
            // 1. Clearing any stored tokens/credentials
            // 2. Clearing session information
            // 3. Notifying your backend if needed
            
            // Since we're not storing credentials locally in this implementation,
            // we just return success. In a real app, you'd clear SharedPreferences,
            // database entries, or other stored authentication data.
            
            Log.d("GoogleSignInHelper", "User signed out successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("GoogleSignInHelper", "Sign-out failed", e)
            Result.failure(e)
        }
    }

    /**
     * Get current user info
     * Note: With Credential Manager API, this method signature is kept for compatibility
     * but the intent parameter is not used in the new implementation.
     */
    suspend fun getCurrentUser(intent: Intent?): GoogleSignInResult? {
        return try {
            // In a real app, you would retrieve stored credentials or check current session
            // For now, we return null as we don't persist credentials locally
            // You might want to store the GoogleSignInResult in SharedPreferences or similar
            // after a successful sign-in and retrieve it here
            
            Log.d("GoogleSignInHelper", "Getting current user - not implemented for Credential Manager")
            null
        } catch (e: Exception) {
            Log.e("GoogleSignInHelper", "Failed to get current user", e)
            null
        }
    }
}

/**
 * Data class representing Google Sign-In result
 */
data class GoogleSignInResult(
    val idToken: String,
    val displayName: String,
    val email: String,
    val isSuccess: Boolean
)

/**
 * Composable for Google Sign-In button with enhanced UI
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
            // Google-style icon (using AccountCircle as a placeholder)
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
