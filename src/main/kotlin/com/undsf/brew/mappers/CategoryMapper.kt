package com.undsf.brew.mappers

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.undsf.brew.models.po.Category
import org.apache.ibatis.annotations.Mapper

@Mapper
interface CategoryMapper : BaseMapper<Category>