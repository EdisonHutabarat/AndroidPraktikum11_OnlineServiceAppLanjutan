package id.ac.polbeng.edisonrizal.onlineservice.models

data class LoginResponse (
    val message: String,
    val error: Boolean,
    val data: User
)
