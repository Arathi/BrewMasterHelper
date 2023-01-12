package com.undsf.brew.mappers

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.undsf.brew.models.po.Ingredient
import org.apache.ibatis.annotations.Mapper

@Mapper
interface IngredientMapper : BaseMapper<Ingredient> {
}