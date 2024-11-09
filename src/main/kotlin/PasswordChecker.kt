class PasswordChecker(private val requiredConditions: List<PasswordCondition>,
                      private val strengthFunction: StrengthFunction = StrengthFunction.DEFAULT) {

    fun check(password: String): PasswordCheckResult {
        val failedConditions = requiredConditions.filter { !it.check(password) }.map { it.name }
        return PasswordCheckResult(failedConditions.isEmpty(), strengthFunction.calculate(password), failedConditions)
    }
}

data class PasswordCheckResult(val isRespectingConditions: Boolean, val strength: Int, val failedConditions: List<String>) {
    override fun toString(): String {
        return if (isRespectingConditions) {
            "Password respect conditions, strength: $strength"
        } else {
            "Password don't respect conditions, strength: $strength\nFailed conditions: \n - ${failedConditions.joinToString("\n - ")}"
        }
    }
}

data class PasswordCondition(val name: String, val check: (String) -> Boolean) {
    companion object {
        fun lengthBetween(min: Int, max: Int) = PasswordCondition("Length must be between $min and $max characters") { it.length in min..max }
        fun uppercaseLetter() = PasswordCondition("Must contain an uppercase letter") { it.any { it.isUpperCase() } }
        fun lowercaseLetter() = PasswordCondition("Must contain an lowercase letter") { it.any { it.isLowerCase() } }
        fun digit() = PasswordCondition("Must contain a digit") { it.any { it.isDigit() } }
        fun specialCharacter() = PasswordCondition("Must contain a special character") { it.any { !it.isLetterOrDigit() } }
        fun noCommonWords(words: List<String>) = PasswordCondition("Must not contain common words") { password ->
            words.none { password.contains(it, ignoreCase = true) }
        }
        fun noSequence() = PasswordCondition("Must not contain a character sequence") { password ->
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
        fun regex(name: String, regex: Regex) = PasswordCondition(name) { regex.containsMatchIn(it) }
        fun noRegex(name: String, regex: Regex) = PasswordCondition(name) { !regex.containsMatchIn(it) }
    }
}

fun interface StrengthFunction {
    fun calculate(password: String): Int

    companion object {
        val DEFAULT = StrengthFunction { password -> password.length }
    }
}