package com.vincie.com.vincie.controller

import com.vincie.com.vincie.model.CombinedRatingsTable
import com.vincie.com.vincie.model.Bilateral
import com.vincie.com.vincie.model.Rating
import kotlin.math.roundToInt

class CombinedRatingService(
    private val ratingsTable: CombinedRatingsTable
) {

    val report = mutableListOf<String>()

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
        report.add("Looking for bilateral arm ratings...")
        bilateralRatings.addAll(combineRatings(leftArm,rightArm))

        //leg
        report.add("Looking for bilateral leg ratings...")
        bilateralRatings.addAll(combineRatings(leftLeg,rightLeg))

        return bilateralRatings;
    }

    private fun combineRatings(left: List<Rating>, right: List<Rating>): List<Rating> {
        if (left.isNotEmpty() && right.isNotEmpty()) {
            val combined = (left + right).sortedBy { it.awardPercentage.value }
            val pairs = combined.size / 2
            report.add("$pairs pairs of bilateral ratings found")
            return combined.subList(0, pairs)
        }
        report.add("No bilateral ratings found.")
        return listOf()
    }

    /**
     * rounds an Int to the nearest 10's place
     */
    fun finalRounding(input: Int): Int {
        report.add("Rounding from actual final rating of $input")
        return (input.toDouble() / 10).roundToInt() * 10
    }

    fun calculateFinalRating(input: List<Rating>): Int {
        var currentRating = 0
        val orderedRatings = orderBySeverity(input)
        val bilateralRatings = huntForBilateralFactor(orderedRatings)

        for (rating in orderedRatings) {
            if (currentRating == 0) {
                currentRating = rating.awardPercentage.value
                report.add("First rating (most severe) = $rating")
            } else {
                currentRating = ratingsTable.combineRating(currentRating, rating.awardPercentage)!!
                report.add("New combined rating = $currentRating, from adding $rating")
                if (bilateralRatings.contains(rating)) {
                    report.add("Applying bilateral factor from $rating")
                    currentRating = (currentRating * 1.10).toInt()
                }
            }
        }
        val finalRoundedRating = finalRounding(currentRating)
        report.add("To final rating of $finalRoundedRating")
        return finalRoundedRating
    }

    fun printReport() { report.forEach{ println(it) }}
}