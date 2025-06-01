package com.example.plantapp

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.plantnetapp.R
import com.example.plantnetapp.back.CsvParser
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test

class CSVParserTest {

    private var context: Context? = null

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context?>()
    }

    @Test
    fun parseTest(){
        val plants = CsvParser.parse(context, R.raw.data_1744126677780)
        assertEquals(true,plants != null && plants.isNotEmpty());
    }
}