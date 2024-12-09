package com.vincie.controller

import com.vincie.com.vincie.controller.CombinedRatingService
import com.vincie.com.vincie.model.AwardPercentage
import com.vincie.com.vincie.model.CombinedRatingsTable
import com.vincie.com.vincie.model.Extremity
import com.vincie.com.vincie.model.Rating
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.`in`
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.test.fail

class CombinedRatingServiceTest {

    val subject = CombinedRatingService(CombinedRatingsTable())

    @Test
    fun `orderBySeverity, given proper order, returns input`() {
        val input = listOf(
            Rating(Extremity.LEFT_ARM,AwardPercentage.SEVENTY),
            Rating(Extremity.LEFT_ARM,AwardPercentage.TEN)
        )

        assertThat(subject.orderBySeverity(input)).containsExactly(
            Rating(Extremity.LEFT_ARM,AwardPercentage.SEVENTY),
            Rating(Extremity.LEFT_ARM,AwardPercentage.TEN)
        )
    }

    @Test
    fun `orderBySeverity, given backwards order, returns expected`() {
        val input = listOf(
            Rating(Extremity.LEFT_ARM,AwardPercentage.TEN),
            Rating(Extremity.LEFT_ARM,AwardPercentage.FORTY),
            Rating(Extremity.LEFT_ARM,AwardPercentage.NINETY)
        )

        assertThat(subject.orderBySeverity(input)).containsExactly(
            Rating(Extremity.LEFT_ARM,AwardPercentage.NINETY),
            Rating(Extremity.LEFT_ARM,AwardPercentage.FORTY),
            Rating(Extremity.LEFT_ARM,AwardPercentage.TEN)
        )
    }

    @Test
    fun `orderBySeverity, given scrambled order, returns expected`() {
        val input = listOf(
            Rating(Extremity.LEFT_ARM,AwardPercentage.TEN),
            Rating(Extremity.LEFT_ARM,AwardPercentage.SEVENTY),
            Rating(Extremity.LEFT_ARM,AwardPercentage.FORTY),
            Rating(Extremity.LEFT_ARM,AwardPercentage.NINETY),
            Rating(Extremity.LEFT_ARM,AwardPercentage.FORTY)
        )

        assertThat(subject.orderBySeverity(input)).containsExactly(
            Rating(Extremity.LEFT_ARM,AwardPercentage.NINETY),
            Rating(Extremity.LEFT_ARM,AwardPercentage.SEVENTY),
            Rating(Extremity.LEFT_ARM,AwardPercentage.FORTY),
            Rating(Extremity.LEFT_ARM,AwardPercentage.FORTY),
            Rating(Extremity.LEFT_ARM,AwardPercentage.TEN)
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
            Rating(Extremity.LEFT_ARM,AwardPercentage.FORTY)
        )

        assertThat(subject.calculateFinalRating(input)).isEqualTo(40)
    }

    @Test
    fun `calculateFinalRating, given two 10s without bilateral factor, returns 20`() {
        val input = listOf(
            Rating(Extremity.LEFT_ARM,AwardPercentage.TEN),
            Rating(Extremity.RIGHT_LEG,AwardPercentage.TEN)
        )

        assertThat(subject.calculateFinalRating(input)).isEqualTo(20)
    }

    @Test
    fun `calculateFinalRating, given the example in 4-25, returns expected`() {
        val input = listOf(
            Rating(Extremity.LEFT_ARM,AwardPercentage.FORTY),
            Rating(Extremity.LEFT_ARM,AwardPercentage.TWENTY),
            Rating(Extremity.LEFT_ARM,AwardPercentage.SIXTY)
        )

        assertThat(subject.calculateFinalRating(input)).isEqualTo(80)
    }

    @Test
    fun `huntForBilateralFactor, given bilateral list, returns 2nd rating of that type`() {
        val input = listOf(
            Rating(Extremity.NOT_APPLICABLE,AwardPercentage.SEVENTY),
            Rating(Extremity.LEFT_ARM, AwardPercentage.TWENTY),
            Rating(Extremity.RIGHT_ARM,AwardPercentage.TEN)
        )

        assertThat(subject.huntForBilateralFactor(input)).containsOnly(
            Rating(Extremity.RIGHT_ARM,AwardPercentage.TEN)
        )
    }

    @Test
    fun `calculateFinalRating, given example for bilateral factor in 5-1-5-5, returns expected`() {
        val input = listOf(
            Rating(Extremity.LEFT_ARM,AwardPercentage.THIRTY),
            Rating(Extremity.RIGHT_ARM,AwardPercentage.TEN),
            Rating(Extremity.NOT_APPLICABLE,AwardPercentage.TEN)
        )

        assertThat(subject.calculateFinalRating(input)).isEqualTo(50)
    }
    
    //TODO report
}