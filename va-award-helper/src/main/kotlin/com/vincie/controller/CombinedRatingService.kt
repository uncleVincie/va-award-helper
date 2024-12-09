package com.vincie.com.vincie.controller

import com.vincie.com.vincie.model.CombinedRatingsTable
import com.vincie.com.vincie.model.Extremity
import com.vincie.com.vincie.model.Rating
import kotlin.math.roundToInt

class CombinedRatingService(
    private val ratingsTable: CombinedRatingsTable
) {

    /**
     * orders ratings by most severe to least, based on the value of the awardPercentage therein
     */
    fun orderBySeverity(input: List<Rating>): List<Rating> = input.sortedByDescending { it.awardPercentage.value }

    /**
     * determines if the veteran qualifies for the bilateral factor (disabled in left and right arm or left and right leg)
     * assumes that the bilateral factor is applied after the last award percentage for that extremity (arm or leg)
     * assumes that ratings are already input in order of awardPercentage
     */
    fun huntForBilateralFactor(input: List<Rating>): List<Rating> {
        val bilateralRatings: MutableList<Rating> = mutableListOf()

        val leftArm = input.filter { it.extremity == Extremity.LEFT_ARM }
        val rightArm = input.filter { it.extremity == Extremity.RIGHT_ARM }
        val leftLeg = input.filter { it.extremity == Extremity.LEFT_LEG }
        val rightLeg = input.filter { it.extremity == Extremity.RIGHT_LEG }

        //arm
        if (leftArm.isNotEmpty() && rightArm.isNotEmpty()) {
            val combined = (leftArm + rightArm).sortedByDescending { it.awardPercentage.value }
            bilateralRatings.add(combined.last())
        }

        //leg
        if (leftLeg.isNotEmpty() && rightLeg.isNotEmpty()) {
            val combined = (leftLeg + rightLeg).sortedByDescending { it.awardPercentage.value }
            bilateralRatings.add(combined.last())
        }

        return bilateralRatings;
    }

    /**
     * rounds an Int to the nearest 10's place
     */
    fun finalRounding(input: Int): Int {
        return (input.toDouble() / 10).roundToInt() * 10
    }

    fun calculateFinalRating(input: List<Rating>): Int {
        var currentRating = 0
        val orderedRatings = orderBySeverity(input)
        val bilateralRatings = huntForBilateralFactor(orderedRatings)

        for (rating in orderedRatings) {
            if (currentRating == 0) {
                currentRating = rating.awardPercentage.value
            } else {
                currentRating =  ratingsTable.combineRating(currentRating,rating.awardPercentage)!!
                if (bilateralRatings.contains(rating)) {
                    currentRating = (currentRating * 1.10).toInt()
                }

            }
        }

        return finalRounding(currentRating)
    }
}