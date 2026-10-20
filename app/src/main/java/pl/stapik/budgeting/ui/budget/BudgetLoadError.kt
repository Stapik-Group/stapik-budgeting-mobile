package pl.stapik.budgeting.ui.budget

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import pl.stapik.budgeting.R
import pl.stapik.budgeting.data.repository.BudgetNotFoundException
import pl.stapik.common.i18n.Messages
import pl.stapik.common.sync.CloudError
import pl.stapik.common.sync.CloudException
import pl.stapik.common.sync.UnreadableKind

sealed interface BudgetLoadError {
    data object NoNetwork : BudgetLoadError
    data object Unauthorized : BudgetLoadError
    data object NotFound : BudgetLoadError

    /** The cloud data cannot be read (encrypted, wrong passphrase, newer format); [messageKey] is a library message key. */
    data class Unreadable(val messageKey: String) : BudgetLoadError

    data class Unknown(val message: String) : BudgetLoadError
}

fun Throwable.toBudgetLoadError(): BudgetLoadError = when (this) {
    is BudgetNotFoundException -> BudgetLoadError.NotFound
    is CloudException -> when (val cloud = error) {
        CloudError.Unauthorized, CloudError.Forbidden -> BudgetLoadError.Unauthorized
        is CloudError.Network -> BudgetLoadError.NoNetwork
        is CloudError.Unreadable -> BudgetLoadError.Unreadable(unreadableKey(cloud.kind))
        is CloudError.ServerError -> BudgetLoadError.Unknown("HTTP ${cloud.httpCode}")
        is CloudError.RateLimited -> BudgetLoadError.Unknown("HTTP 429")
        CloudError.Conflict -> BudgetLoadError.Unknown("Conflict")
    }
    else -> BudgetLoadError.Unknown(message ?: this::class.simpleName ?: "Unknown error")
}

private fun unreadableKey(kind: UnreadableKind): String = when (kind) {
    UnreadableKind.PASSPHRASE_REQUIRED -> "sync.unreadable.passphrase_required"
    UnreadableKind.DECRYPTION_FAILED -> "sync.unreadable.wrong_passphrase"
    UnreadableKind.UNSUPPORTED_VERSION -> "sync.unreadable.newer_version"
    UnreadableKind.OTHER -> "sync.unreadable.other"
}

@Composable
fun BudgetLoadError.toDisplayMessage(): String = when (this) {
    BudgetLoadError.NoNetwork -> stringResource(R.string.error_no_network)
    BudgetLoadError.Unauthorized -> stringResource(R.string.error_unauthorized)
    BudgetLoadError.NotFound -> stringResource(R.string.error_not_found)
    is BudgetLoadError.Unreadable -> Messages.text(LocalContext.current, messageKey)
    is BudgetLoadError.Unknown -> stringResource(R.string.error_unknown, message)
}
