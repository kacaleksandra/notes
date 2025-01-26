package tech.pacia.notes.features.signin

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import tech.pacia.notes.ui.theme.NotesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    onDismissError: () -> Unit = {},
    onSignInSubmitted: (email: String, password: String) -> Unit = { _, _ -> },
    signInState: SignInState = SignInState.Loading,
    onNavigateToSignUp: () -> Unit = {},
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }

    val localFocusManager = LocalFocusManager.current

    Scaffold(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { localFocusManager.clearFocus() },
                )
            },
        topBar = { TopAppBar(title = { Text("Sign in to Notes") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .imePadding()
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(
                modifier = Modifier.padding(8.dp),
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Person,
                        contentDescription = "Email",
                    )
                },
            )

            OutlinedTextField(
                modifier = Modifier.padding(8.dp),
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = "Password",
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                trailingIcon = {
                    IconButton(
                        onClick = { showPassword = !showPassword },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = if (showPassword) "Hide password" else "Show password",
                        )
                    }
                },
            )

            Button(
                onClick = { onSignInSubmitted(email, password) },
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.75f)
                    .padding(8.dp),
            ) {
                Text("Sign in")
            }

            Button(
                onClick = onNavigateToSignUp,
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.75f)
                    .padding(8.dp),
            ) {
                Text("Create new account")
            }

            Spacer(Modifier.weight(1f))

            Button(onClick = {
                startActivity(
                    FlutterActivity.createDefaultIntent(this)
                )
            }) {
                Text("Launch Flutter!")
            }
        }
    }

    when (signInState) {
        is SignInState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Transparent)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }, // This is mandatory
                        onClick = { /* block interactions with other UI */ },
                    ),
            ) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Signing in...",
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator()
                }
            }
        }

        is SignInState.Error -> {
            AlertDialog(
                icon = { Icon(Icons.Rounded.Warning, contentDescription = "Warning icon") },
                title = { Text(text = signInState.message) },
                text = { Text(text = "Failed to sign in") },
                onDismissRequest = onDismissError,
                confirmButton = {
                    TextButton(
                        onClick = onDismissError,
                    ) {
                        Text("Try again")
                    }
                },
            )
        }

        else -> Unit
    }
}

@Preview(uiMode = UI_MODE_NIGHT_NO, showSystemUi = true)
@Composable
fun SignInScreenPreview() {
    NotesTheme {
        SignInScreen()
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES, showSystemUi = true)
@Composable
fun SignInScreenInProgressPreview() {
    NotesTheme {
        SignInScreen()
    }
}
