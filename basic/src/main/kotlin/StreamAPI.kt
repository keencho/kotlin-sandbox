import utils.isTrue
import java.util.stream.IntStream

data class User(
    val loginId: String,
    val password: String,
    val name: String,
    val age: Int,
) {
    fun isLoginIdContainsEven(prefix: String): Boolean {
        if (loginId.isBlank()) return false

        val split = loginId.split(prefix)
        if (split.size == 1) return false

        val num = split[1].toInt()

        return num % 2 == 0
    }
}

// A~Z, a~z, 0~9 문자를 사용하여 길이가 length 인 랜덤한 string 생성
fun getRandomString(length: Int): String = (1..length).map { (('A'..'Z') + ('a'..'z') + ('0'..'9')).random() }.joinToString("")

fun generateUsers(size: Int): List<User> = IntStream.range(0, size).mapToObj { User("아이디$it", getRandomString(10), "이름$it", it) }.toList()

fun main(args: Array<String>) {

    val size = 100
    val users = generateUsers(size)

    isTrue(users.all { it.loginId.contains("아이디") && it.name.contains("이름") })
    isTrue(users.none { it.loginId.contains(size.toString()) }, "로그인 아이디에 '100' 이 포함된 유저가 존재합니다.")
    isTrue(users.filter { it.isLoginIdContainsEven("아이디") }.size == size / 2)
}