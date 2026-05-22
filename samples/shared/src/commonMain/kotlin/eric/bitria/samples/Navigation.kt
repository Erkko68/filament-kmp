package eric.bitria.samples

/** Destinations in the samples app. */
sealed class Screen {
    data object Home : Screen()
    data object Duck : Screen()
    data object Primitives : Screen()
    data object Picking : Screen()
}
