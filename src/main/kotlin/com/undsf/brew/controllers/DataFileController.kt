package com.undsf.brew.controllers

import com.undsf.brew.models.BasicResponse
import com.undsf.brew.models.DataResponse
import com.undsf.brew.models.Ingredient
import com.undsf.brew.services.DataFileService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/data-file")
class DataFileController {
    @Autowired
    lateinit var svc: DataFileService

    @PostMapping("/load")
    fun load(@RequestParam("fileName") fileName: String) : DataResponse<List<Ingredient>> {
        val ingredients = svc.load(fileName)
        return DataResponse(0, "成功", ingredients)
    }
}