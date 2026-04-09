package eric.bitria.samples

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform