package com.example.satunoltechnicaltest

import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader

data class CsvData(val id: Float, val apiHi: Float, val apiLo: Float)

suspend fun readCsvData(inputStream: InputStream, limit: Int): List<CsvData> =
    withContext(Dispatchers.IO) {
        val csvDataList = mutableListOf<CsvData>()
        try {
            val reader = CSVReaderBuilder(InputStreamReader(inputStream))
                .withCSVParser(CSVParserBuilder().withSeparator(';').build())
                .build()

            var line: Array<String> = emptyArray()

            reader.readNext() // Skip header

            var count = 0
            while (count < limit && reader.readNext().also { line = it } != null) {
                val id = line.getOrNull(0)?.toFloatOrNull() ?: 0f
                val apiHi = line.getOrNull(5)?.toFloatOrNull() ?: 0f
                val apiLo = line.getOrNull(6)?.toFloatOrNull() ?: 0f
                csvDataList.add(CsvData(id, apiHi, apiLo))
                count++
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        println("CSVDataList: $csvDataList")
        csvDataList
    }
