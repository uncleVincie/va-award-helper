package com.vincie.controller

import com.vincie.model.CombinedRatingsTable
import com.vincie.model.Rating
import java.io.File
import java.io.FileWriter
import kotlin.math.min
import kotlin.math.round
import kotlin.math.roundToInt

private const val BILATERAL_MULT = 1.10

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
    fun calculateBilateralKicker(left: Rating, right: Rating): Double {
        reportBuffer.add("Calculating bilateral factor for $left and $right")
        val orderedRatings = orderBySeverity(listOf(left, right))
        val combinedRating = ratingsTable.combineRating(orderedRatings[0].awardPercentage.value, orderedRatings[1].awardPercentage) ?: 0
        reportBuffer.add("Combined rating for this bilateral pair WITHOUT multiplier is $combinedRating")
        val bilateralPoints = round(combinedRating*(BILATERAL_MULT-1)*10)/10
        reportBuffer.add("Bilateral factor gives an extra $bilateralPoints %")
        return bilateralPoints
    }

    /**
     * assumes already sorted such that the last unique id in the list is the 2nd to be processed
     */
    fun huntForBilaterals(input: List<Rating>): Double {
        var bilateralSum = 0.0

        val validBilaterals = input.filter { it.bilateralId > 0 }
        val alreadyProcessed = mutableListOf<Int>()
        for (rating in validBilaterals) {
            if (!alreadyProcessed.contains(rating.bilateralId)) {
                val match: Rating = validBilaterals.last { it.bilateralId == rating.bilateralId }
                if (match != rating) {
                    bilateralSum += calculateBilateralKicker(rating, match)
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
        writeReportBuffer()
        return finalRoundedRating
    }

    private fun writeReportBuffer() {
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