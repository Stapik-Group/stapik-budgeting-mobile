package pl.stapik.budgeting.ui.budget

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import pl.stapik.budgeting.R
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed interface BudgetLoadError {
    data object NoNetwork : BudgetLoadError
    data object Unauthorized : BudgetLoadError
    data object NotFound : BudgetLoadError
    data object CannotVerify : BudgetLoadError
    data class Unknown(val message: String) : BudgetLoadError
}

fun Throwable.toBudgetLoadError(): BudgetLoadError = when (this) {
    is UnknownHostException, is SocketTimeoutException -> BudgetLoadError.NoNetwork
    is HttpException -> when (code()) {
        401, 403 -> BudgetLoadError.Unauthorized
        404 -> BudgetLoadError.NotFound
        else -> BudgetLoadError.Unknown(message())
    }
    else -> BudgetLoadError.Unknown(message ?: this::class.simpleName ?: "Unknown error")
}

@Composable
fun BudgetLoadError.toDisplayMessage(): String = when (this) {
    BudgetLoadError.NoNetwork -> stringResource(R.string.error_no_network)
    BudgetLoadError.Unauthorized -> stringResource(R.string.error_unauthorized)
    BudgetLoadError.NotFound -> stringResource(R.string.error_not_found)
    BudgetLoadError.CannotVerify -> stringResource(R.string.error_cannot_verify)
    is BudgetLoadError.Unknown -> stringResource(R.string.error_unknown, message)
}
