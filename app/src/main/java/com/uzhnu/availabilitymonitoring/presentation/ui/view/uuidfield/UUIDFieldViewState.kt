package com.uzhnu.availabilitymonitoring.presentation.ui.view.uuidfield

import androidx.annotation.StringRes
import com.uzhnu.availabilitymonitoring.R

data class UUIDFieldViewState(val text: String = "", private val error: UUIDFieldErrors? = null) {

    @StringRes
    fun labelText(): Int {
        return error?.messageId ?: R.string.uuid_input_name
    }

    fun isError(): Boolean = error != null

    fun isButtonEnabled(): Boolean = error == null && text.isNotEmpty()
}


enum class UUIDFieldErrors constructor(
    @StringRes
    val messageId: Int
) {
    NotValidId(R.string.uuid_error_wrong),
    LinkEntered(R.string.uuid_error_entered_full_link)
}
