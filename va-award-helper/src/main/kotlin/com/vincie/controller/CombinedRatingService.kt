package com.vincie.controller

import com.vincie.model.CombinedRatingsTable
import com.vincie.model.Bilateral
import com.vincie.model.Rating
import java.io.File
import java.io.FileWriter
import kotlin.math.roundToInt

private const val BILATERAL_MULT = 1.10

class CombinedRatingService(
    private val ratingsTable: CombinedRatingsTable
) {

    val reportBuffer = mutableListOf<String>()
    var finalReport = listOf<String>()

    /**
     * orders ratings by most severe to least, based on the value of the awardPercentage therein
     */
    fun orderBySeverity(input: List<Rating>): List<Rating> = input.sortedByDescending { it.awardPercentage.value }

    /**
     * determines if the veteran qualifies for the bilateral factor (disabled in left and right arm or left and right leg)
     * assumes that the bilateral factor is applied after the last award percentage for that extremity (arm or leg)
     * assumes that bilateral ratings are applied to the lowest ratings in the list
     */
    fun huntForBilateralFactor(input: List<Rating>): List<Rating> {
        val bilateralRatings: MutableList<Rating> = mutableListOf()

        val leftArm = input.filter { it.bilateral == Bilateral.LEFT_ARM }
        val rightArm = input.filter { it.bilateral == Bilateral.RIGHT_ARM }
        val leftLeg = input.filter { it.bilateral == Bilateral.LEFT_LEG }
        val rightLeg = input.filter { it.bilateral == Bilateral.RIGHT_LEG }

        //arm
        reportBuffer.add("Looking for bilateral arm ratings...")
        bilateralRatings.addAll(combineRatings(leftArm, rightArm))

        //leg
        reportBuffer.add("Looking for bilateral leg ratings...")
        bilateralRatings.addAll(combineRatings(leftLeg, rightLeg))

        return bilateralRatings;
    }

    private fun combineRatings(left: List<Rating>, right: List<Rating>): List<Rating> {
        if (left.isNotEmpty() && right.isNotEmpty()) {
            val combined = (left + right).sortedBy { it.awardPercentage.value }
            val pairs = combined.size / 2
            reportBuffer.add("$pairs pairs of bilateral ratings found")
            return combined.subList(0, pairs)
        }
        reportBuffer.add("No bilateral ratings found.")
        return listOf()
    }

    /**
     * rounds an Int to the nearest 10's place
     */
    fun finalRounding(input: Int): Int {
        reportBuffer.add("Rounding from actual final rating of $input")
        return (input.toDouble() / 10).roundToInt() * 10
    }

    fun calculateFinalRating(input: List<Rating>): Int {
        reportBuffer.add("Starting New Rating Calculation:")
        var currentRating = 0
        val orderedRatings = orderBySeverity(input)
        val bilateralRatings = huntForBilateralFactor(orderedRatings)

        for (rating in orderedRatings) {
            if (currentRating == 0) {
                currentRating = rating.awardPercentage.value
                reportBuffer.add("First rating (most severe) = $rating")
            } else {
                currentRating = ratingsTable.combineRating(currentRating, rating.awardPercentage)
                    ?: 100 //if table returns null, then x value was already >= 100
                reportBuffer.add("New combined rating = $currentRating, from adding $rating")
                if (bilateralRatings.contains(rating) && currentRating < 100) {
                    reportBuffer.add("Applying bilateral factor from $rating")
                    currentRating = (currentRating * BILATERAL_MULT).toInt()
                }
            }
        }
        val finalRoundedRating = finalRounding(currentRating)
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
        println(sb.toString())
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