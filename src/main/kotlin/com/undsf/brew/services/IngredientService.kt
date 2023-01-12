package com.undsf.brew.services

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.undsf.brew.mappers.IngredientMapper
import com.undsf.brew.models.po.Ingredient
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class IngredientService {
    @Autowired
    lateinit var mapper: IngredientMapper

    @Autowired
    lateinit var flavorSvc: FlavorService

    fun save(ingredient: Ingredient) : Boolean {
        val inserted = mapper.insert(ingredient)
        return inserted == 1
    }

    fun getById(id: Int, withFlavors: Boolean = false) : Ingredient? {
        val ingredient = mapper.selectById(id)
        if (ingredient != null && withFlavors) {
            val flavors = flavorSvc.getByIngredientId(id)
            for (flavor in flavors) {
                ingredient.addFlavor(flavor)
            }
        }
        return ingredient
    }

    fun getAll(withFlavors: Boolean = false) : List<Ingredient> {
        val query = QueryWrapper<Ingredient>()
        val list = mapper.selectList(query)
        if (withFlavors) {
            addFlavors(list)
        }
        return list
    }

    private fun addFlavors(ingredients: List<Ingredient>) {
        // 建立缓存
        val dict = mutableMapOf<Int, Ingredient>()
        for (ingredient in ingredients) {
            dict[ingredient.id] = ingredient
        }

        // 查询风味
        val flavors = flavorSvc.getAll()
        for (flavor in flavors) {
            val ingredient = dict[flavor.ingredientId]
            if (ingredient == null) {
                logger.warn { "未找到id为${flavor.ingredientId}的原料" }
                continue
            }
            ingredient.addFlavor(flavor)
        }
    }
}