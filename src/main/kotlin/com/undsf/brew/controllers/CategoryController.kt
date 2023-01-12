package com.undsf.brew.controllers

import com.undsf.brew.models.DataResponse
import com.undsf.brew.models.po.Category
import com.undsf.brew.services.CategoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/category")
class CategoryController {
    @Autowired
    lateinit var svc: CategoryService

    @GetMapping("")
    fun getAll() : DataResponse<List<Category>> {
        val list = svc.fetchAll()
        return DataResponse(0, "成功", list)
    }

    @GetMapping("/main-categories")
    fun getMainCategories() : DataResponse<List<Category>> {
        val list = svc.fetchMainCategories()
        return DataResponse(0, "成功", list)
    }

    @GetMapping("/subcategories")
    fun getSubcategories() : DataResponse<List<Category>> {
        val list = svc.fetchSubcategories()
        return DataResponse(0, "成功", list)
    }

    @GetMapping("/subcategories/{mainCategoryId}")
    fun getSubcategories(@PathVariable mainCategoryId: Int) :
            DataResponse<List<Category>> {
        val list = svc.fetchSubcategories(mainCategoryId)
        return DataResponse(0, "成功", list)
    }
}