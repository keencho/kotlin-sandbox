package utils

fun isTrue(condition: Boolean) = isTrue(condition, "condition is not true")

fun isTrue(condition: Boolean, message: String) {
    if (!condition) {
        throw RuntimeException(message)
    }
}