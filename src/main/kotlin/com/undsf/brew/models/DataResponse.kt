package com.undsf.brew.models

open class DataResponse<D>(
    var code: Int,
    var message: String?,
    var data: D? = null
)