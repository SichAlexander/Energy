package com.uzhnu.availabilitymonitoring.data.network.model

enum class StatusCode(val code: Int) {
    OK(code = 200),

    BadRequest(code = 400),
    NotFound(code = 404)

}
