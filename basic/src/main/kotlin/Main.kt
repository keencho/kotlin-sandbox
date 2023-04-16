import dto.User
import java.util.stream.IntStream

fun main(args: Array<String>) {

    val idPrefix = "아이디"
    val listSize = 100

    val list = ArrayList<User>()
    IntStream.range(0, listSize).forEach { idx: Int ->
        list.add(User("$idPrefix$idx", getRandomString(10), "이름$idx", idx))
    }

    val match1 = list.all { user: User ->
        user.loginId.contains(idPrefix) && user.name.contains("이름")
    }

    println(match1)

    val filtering = list.filter { it.isLoginIdContainsEven(idPrefix) }

    println(filtering.size == listSize / 2)
}

fun getRandomString(length: Int) : String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}