package com.example.traindriver

import com.example.traindriver.ui.screen.viewing_route_screen.element.startIndexLastWord
import org.junit.Assert
import org.junit.Test

class ViewingRouteUnitTest {
    @Test
    fun startIndexLastWordTest(){
        val text = "Как дела"
        val index = startIndexLastWord(text)
        Assert.assertEquals(4, index)
    }

    @Test
    fun lengthLastWordTest(){
        val text = "Изменить это время можно в настройках"
        val startIndex = startIndexLastWord(text)
        val endIndex = text.length
        val lengthWord = endIndex - startIndex
        Assert.assertEquals(10, lengthWord)
    }
}