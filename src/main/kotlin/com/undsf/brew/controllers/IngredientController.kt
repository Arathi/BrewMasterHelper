package com.undsf.brew.controllers

import com.undsf.brew.models.DataResponse
import com.undsf.brew.models.po.Ingredient
import com.undsf.brew.services.IngredientService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/ingredient")
class IngredientController {
    @Autowired
    lateinit var svc: IngredientService

    @GetMapping("")
    fun getAll(@RequestParam("withFlavors", required = false) withFlavors: Boolean = false) :
            DataResponse<List<Ingredient>> {
        val ingredients = svc.getAll(withFlavors)
        if (ingredients.isNotEmpty()) {
            return DataResponse(0, "成功", ingredients)
        }
        return DataResponse(1, "未找到原料")
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Int,
                @RequestParam("withFlavors", required = false) withFlavors: Boolean = false) :
            DataResponse<Ingredient> {
        val ingredient = svc.getById(id, true)
        if (ingredient != null) {
            return DataResponse(0, "成功", ingredient)
        }
        return DataResponse(1, "未找到id为${id}的原料")
    }
}