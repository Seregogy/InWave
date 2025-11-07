data class SocialMedia (
    val name: String,
    val link: String,
    val imageUrl: String,
)

data class Artist (
    val id: String,
    val name: String,
    val about: String,
    val listentingInMonth: String,
    val likes: String,
    val images: List<String>,
    val socialMedia: String,
)
