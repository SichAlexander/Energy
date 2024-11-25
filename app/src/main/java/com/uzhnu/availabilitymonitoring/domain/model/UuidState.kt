package com.uzhnu.availabilitymonitoring.domain.model

sealed class UuidState {
    object EmptyUuid : UuidState()
    object NotValidUuid : UuidState()
    data class CorrectUuid(val uuid: String) : UuidState()
    object NoConnection : UuidState()
    object NoInternet : UuidState()
    data class UnknownError(val throwable: Throwable) : UuidState()
}

sealed class ExistingUuidState {
    object EmptyUuid : ExistingUuidState()
    data class CorrectUuid(val uuid: String) : ExistingUuidState()
    data class UnknownError(val throwable: Throwable) : ExistingUuidState()
}

