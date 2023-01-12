package com.undsf.brew.services

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.undsf.brew.mappers.FlavorMapper
import com.undsf.brew.models.po.Flavor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FlavorService {
    @Autowired
    lateinit var mapper: FlavorMapper

    fun save(flavor: Flavor) : Boolean {
        val inserted = mapper.insert(flavor)
        return inserted == 1
    }

    fun getById(id: Int) : Flavor {
        return mapper.selectById(id)
    }

    fun getAll() : List<Flavor> {
        val query = QueryWrapper<Flavor>()
        return mapper.selectList(query)
    }

    fun getByIngredientId(ingredientId: Int) : List<Flavor> {
        val query = QueryWrapper<Flavor>()
            .gt("id", ingredientId * 1000)
            .lt("id", (ingredientId+1) * 1000)
        return mapper.selectList(query)
    }
}