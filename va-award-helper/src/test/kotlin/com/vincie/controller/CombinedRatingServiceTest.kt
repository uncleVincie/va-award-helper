package com.vincie.controller

import com.vincie.model.AwardPercentage
import com.vincie.model.CombinedRatingsTable
import com.vincie.model.Bilateral
import com.vincie.model.Rating
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CombinedRatingServiceTest {

    private val subject = CombinedRatingService(CombinedRatingsTable())

    @Test
    fun `orderBySeverity, given proper order, returns input`() {
        val input = listOf(
            Rating(Bilateral.LEFT_ARM, AwardPercentage.SEVENTY),
            Rating(Bilateral.LEFT_ARM, AwardPercentage.TEN)
        )

        assertThat(subject.orderBySeverity(input)).containsExactly(
            Rating(Bilateral.LEFT_ARM, AwardPercentage.SEVENTY),
            Rating(Bilateral.LEFT_ARM, AwardPercentage.TEN)
        )
    }

    @Test
    fun `orderBySeverity, given backwards order, returns expected`() {
        val input = listOf(
            Rating(Bilateral.LEFT_ARM, AwardPercentage.TEN),
            Rating(Bilateral.LEFT_ARM, AwardPercentage.FORTY),
            Rating(Bilateral.LEFT_ARM, AwardPercentage.NINETY)
        )

        assertThat(subject.orderBySeverity(input)).containsExactly(
            Rating(Bilateral.LEFT_ARM, AwardPercentage.NINETY),
            Rating(Bilateral.LEFT_ARM, AwardPercentage.FORTY),
            Rating(Bilateral.LEFT_ARM, AwardPercentage.TEN)
        )
    }

    @Test
    fun `orderBySeverity, given scrambled order, returns expected`() {
        val input = listOf(
            Rating(Bilateral.LEFT_ARM, AwardPercentage.TEN),
            Rating(Bilateral.LEFT_ARM, AwardPercentage.SEVENTY),
            Rating(Bilateral.LEFT_ARM, AwardPercentage.FORTY),
            Rating(Bilateral.LEFT_ARM, AwardPercentage.NINETY),
            Rating(Bilateral.LEFT_ARM, AwardPercentage.FORTY)
        )

        assertThat(subject.orderBySeverity(input)).containsExactly(
            Rating(Bilateral.LEFT_ARM, AwardPercentage.NINETY),
            Rating(Bilateral.LEFT_ARM, AwardPercentage.SEVENTY),
            Rating(Bilateral.LEFT_ARM, AwardPercentage.FORTY),
            Rating(Bilateral.LEFT_ARM, AwardPercentage.FORTY),
            Rating(Bilateral.LEFT_ARM, AwardPercentage.TEN)
        )
    }

    @Test
    fun `finalRounding, given 24, rounds to 20`() {
        assertThat(subject.finalRounding(24)).isEqualTo(20)
    }

    @Test
    fun `finalRounding, given 25, rounds to 30`() {
        assertThat(subject.finalRounding(25)).isEqualTo(30)
    }

    @Test
    fun `finalRounding, given 20, returns 20`() {
        assertThat(subject.finalRounding(20)).isEqualTo(20)
    }

    @Test
    fun `calculateFinalRating, given one rating, returns value of that rating`() {
        val input = listOf(
            Rating(Bilateral.LEFT_ARM, AwardPercentage.FORTY)
        )

        assertThat(subject.calculateFinalRating(input)).isEqualTo(40)
    }

    @Test
    fun `calculateFinalRating, given two 10s without bilateral factor, returns 20`() {
        val input = listOf(
            Rating(Bilateral.LEFT_ARM, AwardPercentage.TEN),
            Rating(Bilateral.RIGHT_LEG, AwardPercentage.TEN)
        )

        assertThat(subject.calculateFinalRating(input)).isEqualTo(20)
    }

    @Test
    fun `calculateFinalRating, given the example in 4-25, returns expected`() {
        val input = listOf(
            Rating(Bilateral.LEFT_ARM, AwardPercentage.FORTY),
            Rating(Bilateral.LEFT_ARM, AwardPercentage.TWENTY),
            Rating(Bilateral.LEFT_ARM, AwardPercentage.SIXTY)
        )

        assertThat(subject.calculateFinalRating(input)).isEqualTo(80)
    }

    @Test
    fun `calculateFinalRating, given ratings that total over 100, returns 100`() {
        val input = listOf(
            Rating(Bilateral.NON_BILATERAL, AwardPercentage.NINETY),
            Rating(Bilateral.RIGHT_ARM, AwardPercentage.NINETY),
            Rating(Bilateral.LEFT_ARM, AwardPercentage.NINETY)
        )

        assertThat(subject.calculateFinalRating(input)).isEqualTo(100)
    }

    @Test
    fun `huntForBilateralFactor, given bilateral list, returns 2nd rating of that type`() {
        val input = listOf(
            Rating(Bilateral.NON_BILATERAL, AwardPercentage.SEVENTY),
            Rating(Bilateral.LEFT_ARM, AwardPercentage.TWENTY),
            Rating(Bilateral.RIGHT_ARM, AwardPercentage.TEN)
        )

        assertThat(subject.huntForBilateralFactor(input)).containsOnly(
            Rating(Bilateral.RIGHT_ARM, AwardPercentage.TEN)
        )
    }

    @Test
    fun `huntForBilateralFactor, given bilateral list with 2 pairs, returns 2 ratings`() {
        val input = listOf(
            Rating(Bilateral.NON_BILATERAL, AwardPercentage.SEVENTY),
            Rating(Bilateral.LEFT_ARM, AwardPercentage.TWENTY),
            Rating(Bilateral.RIGHT_ARM, AwardPercentage.TEN),
            Rating(Bilateral.LEFT_ARM, AwardPercentage.FIFTY),
            Rating(Bilateral.RIGHT_ARM, AwardPercentage.FORTY)
        )

        assertThat(subject.huntForBilateralFactor(input)).containsOnly(
            Rating(Bilateral.RIGHT_ARM, AwardPercentage.TEN),
            Rating(Bilateral.LEFT_ARM, AwardPercentage.TWENTY)
        )
        assertThat(subject.reportBuffer).containsExactly(
            "Looking for bilateral arm ratings...",
            "2 pairs of bilateral ratings found",
            "Looking for bilateral leg ratings...",
            "No bilateral ratings found."
        )
    }

    @Test
    fun `huntForBilateralFactor, given no bilaterals, returns empty`() {
        val input = listOf(
            Rating(Bilateral.NON_BILATERAL, AwardPercentage.SEVENTY),
            Rating(Bilateral.NON_BILATERAL, AwardPercentage.FORTY),
            Rating(Bilateral.LEFT_ARM, AwardPercentage.TWENTY),
            Rating(Bilateral.LEFT_ARM, AwardPercentage.TWENTY),
            Rating(Bilateral.LEFT_LEG, AwardPercentage.TEN),
            Rating(Bilateral.LEFT_LEG, AwardPercentage.FIFTY)
        )

        assertThat(subject.huntForBilateralFactor(input)).isEmpty()
    }

    @Test
    fun `calculateFinalRating, given example for bilateral factor in 5-1-5-5, returns expected`() {
        val input = listOf(
            Rating(Bilateral.LEFT_ARM, AwardPercentage.THIRTY),
            Rating(Bilateral.RIGHT_ARM, AwardPercentage.TEN),
            Rating(Bilateral.NON_BILATERAL, AwardPercentage.TEN)
        )

        assertThat(subject.calculateFinalRating(input)).isEqualTo(50)
        assertThat(subject.finalReport).containsExactly(
            "Starting New Rating Calculation:",
            "Looking for bilateral arm ratings...",
            "1 pairs of bilateral ratings found",
            "Looking for bilateral leg ratings...",
            "No bilateral ratings found.",
            "First rating (most severe) = Rating(bilateral=LEFT_ARM, awardPercentage=THIRTY)",
            "New combined rating = 37, from adding Rating(bilateral=RIGHT_ARM, awardPercentage=TEN)",
            "Applying bilateral factor from Rating(bilateral=RIGHT_ARM, awardPercentage=TEN)",
            "New combined rating = 46, from adding Rating(bilateral=NON_BILATERAL, awardPercentage=TEN)",
            "Rounding from actual final rating of 46",
            "To final rating of 50"
        )
    }

    @Test
    fun `calculateFinalRating, having been called multiple times, report only contains last calculation`() {
        val input1 = listOf(
            Rating(Bilateral.LEFT_ARM, AwardPercentage.TWENTY),
            Rating(Bilateral.RIGHT_ARM, AwardPercentage.TWENTY)
        )

        val input2 = listOf(
            Rating(Bilateral.NON_BILATERAL, AwardPercentage.SEVENTY)
        )

        subject.calculateFinalRating(input1)
        subject.calculateFinalRating(input2)

        assertThat(subject.finalReport).containsExactly(
            "Starting New Rating Calculation:",
            "Looking for bilateral arm ratings...",
            "No bilateral ratings found.",
            "Looking for bilateral leg ratings...",
            "No bilateral ratings found.",
            "First rating (most severe) = Rating(bilateral=NON_BILATERAL, awardPercentage=SEVENTY)",
            "Rounding from actual final rating of 70",
            "To final rating of 70"
        )
    }

    //TODO add 2 bilateral factors (two arms, need to calculate these by hand first)
}