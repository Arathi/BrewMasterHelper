package com.undsf.brew.services

import mu.KotlinLogging
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

private val logger = KotlinLogging.logger {}

@DisplayName("数据文件")
@SpringBootTest
class DataFileServiceTests {
    @Autowired
    lateinit var svc: DataFileService

    @DisplayName("加载")
    @Test
    fun testLoad() {
        svc.open("酿造大师笔记.xlsx")
        val ingredients = svc.load()
        logger.info { "加载完成，获取原料数据${ingredients.size}条" }
    }
}