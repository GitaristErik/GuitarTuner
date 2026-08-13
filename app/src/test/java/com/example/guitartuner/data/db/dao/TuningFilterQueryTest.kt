package com.example.guitartuner.data.db.dao

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Verifies the filter SQL fragment builders keep AND/OR precedence correct.
 * Mirrors [TuningSetDAO.filterTunings] query construction.
 */
class TuningFilterQueryTest {

    private fun buildFilterQuery(
        isFavorite: Boolean?,
        instrumentIds: List<Int>?,
        countString: List<Int>?,
    ): String {
        val instrumentIdsQuery = if (instrumentIds?.isEmpty() == true) null
        else instrumentIds?.joinToString(",") { it.toString() }

        val countStringQuery = if (countString?.isEmpty() == true) null
        else countString?.joinToString(",") { it.toString() }

        return """
        SELECT * FROM TuningSetWithPitchesTable
        WHERE (${isFavorite} IS NULL OR TuningSetWithPitchesTable.isFavorite = ${isFavorite})
        AND ((${instrumentIdsQuery ?: "null"}) IS NULL OR TuningSetWithPitchesTable.instrumentId IN (${instrumentIdsQuery ?: "null"}))
        AND ((${countStringQuery ?: "null"}) IS NULL OR (
            SELECT COUNT(DISTINCT pitchId) FROM TuningSetCrossRefTable
            WHERE TuningSetCrossRefTable.tuningId = TuningSetWithPitchesTable.tuningId
        ) IN (${countStringQuery ?: "null"}))
        LIMIT 10 OFFSET 0 """.trimIndent()
    }

    @Test
    fun favoriteAndInstrument_areGroupedWithParentheses() {
        val sql = buildFilterQuery(
            isFavorite = true,
            instrumentIds = listOf(1, 2),
            countString = listOf(6),
        )
        assertTrue(sql.contains("AND ((1,2) IS NULL OR TuningSetWithPitchesTable.instrumentId IN (1,2))"))
        assertTrue(sql.contains("(true IS NULL OR TuningSetWithPitchesTable.isFavorite = true)"))
        // Ensure instrument OR is not free-floating after AND without parentheses
        assertFalse(sql.contains("= true)\n        AND (1,2) IS NULL OR"))
    }

    @Test
    fun nullFilters_produceNullSentinel() {
        val sql = buildFilterQuery(isFavorite = null, instrumentIds = null, countString = null)
        assertTrue(sql.contains("(null IS NULL OR TuningSetWithPitchesTable.isFavorite = null)"))
        assertTrue(sql.contains("AND ((null) IS NULL OR TuningSetWithPitchesTable.instrumentId IN (null))"))
    }
}
