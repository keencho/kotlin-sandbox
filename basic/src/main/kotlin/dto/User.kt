package dto

class User(
    val loginId: String,
    val password: String,
    val name: String,
    val age: Int,
) {
    fun isLoginIdContainsEven(prefix: String): Boolean {
        if (loginId.isBlank()) return false

        val split = loginId.split(prefix);
        if (split.size == 1) return false

        val num = split[1].toInt();

        return num % 2 == 0
    }
}