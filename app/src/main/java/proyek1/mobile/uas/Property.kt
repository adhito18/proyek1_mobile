package proyek1.mobile.uas

data class Property(
    val id: Int,
    val name: String,
    val description: String,
    val location: String,
    val price: String,
    val size: String,
    val bedrooms: String,
    val bathrooms: String,
    val photo: String,
    var isFavorite: Boolean = false
)
