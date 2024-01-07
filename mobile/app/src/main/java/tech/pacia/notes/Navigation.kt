package tech.pacia.notes

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import tech.pacia.notes.features.home.HomeRoute
import tech.pacia.notes.features.note.NoteRoute
import tech.pacia.notes.features.signin.SignInRoute

private object Destinations {
    const val SIGN_IN_ROUTE = "signin"
    const val HOME_ROUTE = "home/"
    const val NOTE_ROUTE = "note/{id}"
}

@Composable
fun NotesNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Destinations.SIGN_IN_ROUTE,
    ) {
        composable(Destinations.SIGN_IN_ROUTE) {
            SignInRoute(
                onNavigateToHome = { navController.navigate(Destinations.HOME_ROUTE) },
            )
        }
        composable(Destinations.HOME_ROUTE) {
            HomeRoute(
                onNavigateToNote = { noteId ->
                    navController.navigate("note/${noteId}")
                },
            )
        }
        composable(Destinations.NOTE_ROUTE) {
            val id = it.arguments?.getString("id") ?: "empty"
            NoteRoute(
                id = id,
                onNavigateUp = { navController.popBackStack() },
            )
        }
    }
}
