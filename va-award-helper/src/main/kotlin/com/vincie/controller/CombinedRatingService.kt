package com.vincie.controller

import com.vincie.model.AwardPercentage
import com.vincie.model.BilateralData
import com.vincie.model.CombinedRatingsTable
import com.vincie.model.Rating
import java.io.File
import java.io.FileWriter
import kotlin.math.min
import kotlin.math.round
import kotlin.math.roundToInt

private const val BILATERAL_MULT = 0.10
private const val REPORT_SECTION_DELIMITER = "-------"
private const val MIN_FULL_RATING = 95

class CombinedRatingService(
    private val ratingsTable: CombinedRatingsTable
) {

    private val reportBuffer = mutableListOf<String>()
    var finalReport = listOf<String>()

    /**
     * orders ratings by most severe to least, based on the value of the awardPercentage therein
     */
    fun orderBySeverity(input: List<Rating>): List<Rating> = input.sortedByDescending { it.awardPercentage.value }

    /**
     * takes a pair of ratings, orders them by severity, combines their ratings, then multiplies by the bilateral factor.
     * @return the bilateral multiplier percentage points as a double
     */
    fun calculateBilateralKicker(left: Rating, right: Rating): BilateralData {
        reportBuffer.add("Calculating bilateral factor for $left and $right")
        val orderedRatings = orderBySeverity(listOf(left, right))
        val combinedRating = ratingsTable.combineRating(orderedRatings[0].awardPercentage.value, orderedRatings[1].awardPercentage) ?: 0
        reportBuffer.add("Combined rating for this bilateral pair WITHOUT multiplier is $combinedRating")
        val bilateralPoints = round(combinedRating*BILATERAL_MULT*10)/10
        reportBuffer.add("Bilateral factor gives an extra $bilateralPoints %")
        return BilateralData(
            bilateralKicker = bilateralPoints,
            currentRating = combinedRating
        )
    }

    /**
     * assumes already sorted such that the last unique id in the list is the 2nd to be processed
     */
    fun huntForBilaterals(input: List<Rating>): Double {
        var bilateralSum = 0.0
        var ableBodyPercentage = 1.0

        val validBilaterals = input.filter { it.bilateralId > 0 }
        val alreadyProcessed = mutableListOf<Int>()
        for (rating in validBilaterals) {
            if (!alreadyProcessed.contains(rating.bilateralId)) {
                val match: Rating = validBilaterals.last { it.bilateralId == rating.bilateralId }
                if (match != rating) {
                    val bilateralData = calculateBilateralKicker(rating, match)
                    bilateralSum += round(bilateralData.bilateralKicker * ableBodyPercentage * 10)/10
                    ableBodyPercentage -= bilateralData.currentRating/100.0 //decreases the award for subsequent bilaterals
                }
                alreadyProcessed.add(match.bilateralId)
            }
        }

        return bilateralSum
    }

    /**
     * rounds an Int to the nearest 10's place
     */
    fun finalRounding(input: Int): Int {
        reportBuffer.add("Rounding from actual rating of $input")
        return (input.toDouble() / 10).roundToInt() * 10
    }

    fun calculateFinalRating(input: List<Rating>): Int {
        reportBuffer.add("Starting New Rating Calculation:")
        var currentRating = 0

        val orderedRatings = orderBySeverity(input)
        val bilateralSum = huntForBilaterals(orderedRatings)

        for (rating in orderedRatings) {
            if (currentRating == 0) {
                currentRating = rating.awardPercentage.value
                reportBuffer.add("First rating (most severe) = $rating")
            } else {
                currentRating = ratingsTable.combineRating(currentRating, rating.awardPercentage)
                    ?: 100 //if table returns null, then x value was already >= 100
                reportBuffer.add("New combined rating = $currentRating, from adding $rating")
            }
        }
        reportBuffer.add("Adding sum of all bilateral factors ($bilateralSum)")
        currentRating += bilateralSum.roundToInt()
        val finalRoundedRating = finalRounding(min(100, currentRating))
        reportBuffer.add("To final rating of $finalRoundedRating")
        findOneHundredCombinations(currentRating) //do this after the final rounding so the report reads nicely
        writeReportBuffer()
        return finalRoundedRating
    }

    fun forecastOneHundred(startingCombined: Int, additionalAwardLevel: AwardPercentage): Int {

        var newCombined = startingCombined
        var additionalAwardCount = 0
        while (newCombined < MIN_FULL_RATING) {
            newCombined = ratingsTable.combineRating(newCombined, additionalAwardLevel) ?: 100
            additionalAwardCount++
        }
        return additionalAwardCount
    }

    fun findOneHundredCombinations(currentRating: Int) {

        if (currentRating >= MIN_FULL_RATING) return

        reportBuffer.add("\n${REPORT_SECTION_DELIMITER}ADDITIONAL RATINGS REQUIRED TO GET TO 100%${REPORT_SECTION_DELIMITER}")
        for (awardPercentage in AwardPercentage.entries) {
            val currentForecast = forecastOneHundred(currentRating, awardPercentage)
            if (currentForecast == 1) {
                reportBuffer.add("$awardPercentage or higher: $currentForecast")
                break
            }
            reportBuffer.add("${awardPercentage}: ${forecastOneHundred(currentRating, awardPercentage)}")
        }
    }

    fun writeReportBuffer() {
        finalReport = reportBuffer.toList()
        reportBuffer.clear()
    }

    fun saveReport(selectedFile: File) {
        val fileWriter = FileWriter(appendTxt(selectedFile).absolutePath)
        val sb = StringBuilder()
        finalReport.forEach {
            sb.append(it)
            sb.append("\n")
        }
        fileWriter.write(sb.toString())
        fileWriter.close()
    }

    private fun appendTxt(file: File): File {
        return if (file.absolutePath.endsWith(".txt")) {
            file
        } else {
            File(file.absolutePath + ".txt")
        }
    }

    fun printReport() {
        finalReport.forEach { println(it) }
    }

}