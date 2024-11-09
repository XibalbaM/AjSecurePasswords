import kotlin.math.floor
import kotlin.math.min

val passwordChecker = PasswordChecker(listOf(
    PasswordCondition.lengthBetween(8, 32),
    PasswordCondition.lowercaseLetter(),
    PasswordCondition.uppercaseLetter(),
    PasswordCondition.digit(),
    PasswordCondition.specialCharacter(),
    PasswordCondition.noCommonWords(object {}.javaClass.getResource("/common-passwords.txt")!!.readText().lines()),
    PasswordCondition.noRegex("Must not contain a repetition of characters", Regex("(.)\\1{3,}")),
    PasswordCondition.noSequence()
)) { password ->
    val length = password.length
    val differentCharacters = password.toSet().size
    min(floor((length * differentCharacters) / 16.0).toInt(), 8)
}

fun main() {
    while (true) {
        val password = readln()
        println(passwordChecker.check(password))
    }
}