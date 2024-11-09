import kotlin.math.floor
import kotlin.math.min

val passwordChecker = PasswordChecker(listOf(
    PasswordCondition("Length must be between 8 and 32 characters") { it.length in 8..32 },
    PasswordCondition("Must contain an uppercase letter") { it.any { it.isUpperCase() } },
    PasswordCondition("Must contain an lowercase letter") { it.any { it.isLowerCase() } },
    PasswordCondition("Must contain a digit") { it.any { it.isDigit() } },
    PasswordCondition("Must contain a special character") { it.any { !it.isLetterOrDigit() } },
    PasswordCondition("Must not contain an obvious sequence") { password ->
        val bannedRegexs = listOf(
            Regex("(\\w)\\1{3,}"), // 4 or more identical characters in a row
        )
        bannedRegexs.none { it.containsMatchIn(password) }
    },
    PasswordCondition("Must not contain common words") { password ->
        val bannedWords = object {}.javaClass.getResource("/common-passwords.txt")!!.readText().lines()
        bannedWords.none { password.contains(it, ignoreCase = true) }
    },
    PasswordCondition("Must not contain a character sequence") { password ->
        if (password.length < 4) {
            return@PasswordCondition true
        }
        for (i in 0 until password.length - 4) {
            if (password[i] + 1 == password[i + 1] && password[i + 1] + 1 == password[i + 2] && password[i + 2] + 1 == password[i + 3]) {
                return@PasswordCondition false
            }
        }
        true
    }
)) { password ->
    val length = password.length
    val differentCharacters = password.toSet().size
    min(floor((length * differentCharacters) / 16.0).toInt(), 8)
}

fun main() {
    while (true) {
        val password = readln()
        val check = passwordChecker.check(password)
        if (check.isRespectingConditions) {
            println("Password respect conditions, strength: ${check.strength}")
        } else {
            println("Password don't respect conditions, strength: ${check.strength}")
            println("Failed conditions: \n - ${check.failedConditions.joinToString("\n - ")}")
        }
    }
}