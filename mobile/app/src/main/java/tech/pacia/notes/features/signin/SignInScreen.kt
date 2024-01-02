package tech.pacia.notes.features.signin

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import tech.pacia.notes.ui.theme.NotesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    inProgress: Boolean = false,
    hasError: Boolean = false,
    onDismissError: () -> Unit = {},
    onSignInSubmitted: (email: String, password: String) -> Unit = { _, _ -> },
) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }

    val localFocusManager = LocalFocusManager.current

    Scaffold(
        modifier = modifier
            .pointerInput(Unit) { detectTapGestures(onTap = { localFocusManager.clearFocus() }) },
        topBar = { TopAppBar(title = { Text("Notes") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(
                modifier = Modifier.padding(8.dp),
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Person,
                        contentDescription = "Username",
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
                            // imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.Lock,
                            contentDescription = if (showPassword) "Hide password" else "Show password",
                        )
                    }
                },
            )

            Button(
                onClick = { onSignInSubmitted(username, password) },
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.75f)
                    .padding(8.dp),
            ) {
                Text("Sign in")
            }
        }
    }

    if (inProgress) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Transparent)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }, // This is mandatory
                    onClick = { /* block interactions with other UI */ },
                )
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

    if (hasError) {
        AlertDialog(
            icon = {
                Icon(Icons.Rounded.Warning, contentDescription = "Example Icon")
            },
            title = { Text(text = "dialogTitle") },
            text = { Text(text = "dialogText") },
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

}

@Preview(uiMode = UI_MODE_NIGHT_YES, showSystemUi = true)
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
        SignInScreen(inProgress = true)
    }
}
