package com.undsf.brew.services

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@DisplayName("分类服务")
@SpringBootTest
class CategoryServiceTest {
    @Autowired
    lateinit var svc: CategoryService

    @DisplayName("获取所有分类")
    @Test
    fun testFetchAll() {
        val categories = svc.fetchAll()
        assertEquals(32, categories.size)
    }

    @DisplayName("获取主分类")
    @Test
    fun testFetchMainCategories() {
        val categories = svc.fetchMainCategories()
        assertEquals(6, categories.size)
    }

    @DisplayName("获取子分类")
    @Test
    fun testFetchSubcategories() {
        val hops = svc.fetchSubcategories(1)
        assertEquals(7, hops.size)

        val grains = svc.fetchSubcategories(4)
        assertEquals(6, grains.size)

        val subcategories = svc.fetchSubcategories()
        assertEquals(32-6, subcategories.size)
    }
}