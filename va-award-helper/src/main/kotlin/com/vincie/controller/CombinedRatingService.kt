package com.vincie.com.vincie.controller

import com.vincie.com.vincie.model.CombinedRatingsTable
import com.vincie.com.vincie.model.Rating
import kotlin.math.roundToInt

class CombinedRatingService(
    val ratingsTable: CombinedRatingsTable
) {

    /**
     * orders ratings by most severe to least, based on the value of the awardPercentage therein
     */
    fun orderBySeverity(input: List<Rating>): List<Rating> = input.sortedByDescending { it.awardPercentage.value }

    /**
     * rounds an Int to the nearest 10's place
     */
    fun finalRounding(input: Int): Int {
        return (input.toDouble() / 10).roundToInt() * 10
    }

    fun calculateFinalRating(input: List<Rating>): Int {
        var currentRating = 0

        for (rating in orderBySeverity(input)) {
            if (currentRating == 0) {
                currentRating = rating.awardPercentage.value
            } else {
                currentRating =  ratingsTable.combineRating(currentRating,rating.awardPercentage)!!
            }
        }

        return finalRounding(currentRating)
    }
}