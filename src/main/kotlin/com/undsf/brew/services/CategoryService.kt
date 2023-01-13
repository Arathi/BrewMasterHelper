package com.undsf.brew.services

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.undsf.brew.mappers.CategoryMapper
import com.undsf.brew.models.po.Category
import jakarta.annotation.PostConstruct
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class CategoryService {
    @Autowired
    lateinit var mapper: CategoryMapper

    @PostConstruct
    fun init() {
        logger.info { "正在重建主分类名称缓存" }
        val mainCategories = fetchMainCategories()
        Category.mainCategoryNames.clear()
        for (category in mainCategories) {
            Category.mainCategoryNames[category.id] = category.name
        }

        logger.info { "正在重建子分类全名缓存" }
        val subcatetories = fetchSubcategories()
        Category.fullNames.clear()
        for (category in subcatetories) {
            Category.fullNames[category.id] = category.fullName
        }
    }

    fun fetchAll() : List<Category> {
        val query = QueryWrapper<Category>()
        return mapper.selectList(query)
    }

    fun fetchMainCategories() : List<Category> {
        val query = QueryWrapper<Category>()
            .lt("id", 10)
        return mapper.selectList(query)
    }

    fun fetchSubcategories(mainCategoryId: Int) : List<Category> {
        val query = QueryWrapper<Category>()
            .gt("id", mainCategoryId * 10)
            .lt("id", (mainCategoryId+1) * 10)
        return mapper.selectList(query)
    }

    fun fetchSubcategories() : List<Category> {
        val query = QueryWrapper<Category>()
            .gt("id", 10)
        return mapper.selectList(query)
    }
}