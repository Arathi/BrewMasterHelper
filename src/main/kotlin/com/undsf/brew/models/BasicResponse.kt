package com.undsf.brew.models

class BasicResponse(code: Int, message: String? = null) : DataResponse<Void>(code, message)