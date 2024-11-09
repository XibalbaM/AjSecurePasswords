class PasswordChecker(private val requiredConditions: List<PasswordCondition>,
                      private val strengthFunction: StrengthFunction = StrengthFunction.DEFAULT) {

    fun check(password: String): PasswordCheckResult {
        val failedConditions = requiredConditions.filter { !it.check(password) }.map { it.name }
        return PasswordCheckResult(failedConditions.isEmpty(), strengthFunction.calculate(password), failedConditions)
    }
}

data class PasswordCheckResult(val isRespectingConditions: Boolean, val strength: Int, val failedConditions: List<String>)

data class PasswordCondition(val name: String, val check: (String) -> Boolean)

fun interface StrengthFunction {
    fun calculate(password: String): Int

    companion object {
        val DEFAULT = StrengthFunction { password -> password.length }
    }
}