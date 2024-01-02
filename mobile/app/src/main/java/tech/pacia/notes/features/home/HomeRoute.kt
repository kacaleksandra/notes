package tech.pacia.notes.features.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import tech.pacia.notes.features.signin.SignInScreen
import tech.pacia.notes.features.signin.SignInViewModel

@Composable
fun HomeRoute(
    onNavigateToNote: (noteId: String) -> Unit,
) {
    HomeScreen(
        onNavigateToNote = onNavigateToNote
    )
}
