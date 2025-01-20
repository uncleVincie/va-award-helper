package com.vincie.controller

import com.vincie.model.AwardPercentage
import com.vincie.model.Bilateral
import com.vincie.model.CombinedRatingsTable
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
    fun `calculateFinalRating, given example for bilateral factor in 5-1-5-5, returns expected`() {
        val input = listOf(
            Rating(Bilateral.LEFT_ARM, AwardPercentage.THIRTY, 1),
            Rating(Bilateral.RIGHT_ARM, AwardPercentage.TEN, 1),
            Rating(Bilateral.NON_BILATERAL, AwardPercentage.TEN, 0)
        )

        val actual = subject.calculateFinalRating(input)
        subject.printReport()
        assertThat(actual).isEqualTo(50)
        assertThat(subject.finalReport).containsExactly(
            "Starting New Rating Calculation:",
            "Calculating bilateral factor for Rating(bilateral=LEFT_ARM, awardPercentage=THIRTY, bilateralId=1) and Rating(bilateral=RIGHT_ARM, awardPercentage=TEN, bilateralId=1)",
            "Combined rating for this bilateral pair WITHOUT multiplier is 37",
            "Bilateral factor gives an extra 3.7 %",
            "First rating (most severe) = Rating(bilateral=LEFT_ARM, awardPercentage=THIRTY, bilateralId=1)",
            "New combined rating = 37, from adding Rating(bilateral=RIGHT_ARM, awardPercentage=TEN, bilateralId=1)",
            "New combined rating = 43, from adding Rating(bilateral=NON_BILATERAL, awardPercentage=TEN, bilateralId=0)",
            "Adding sum of all bilateral factors (3.7)",
            "Rounding from actual rating of 47",
            "To final rating of 50"
        )
    }

    @Test
    fun `calculateFinalRating, having been called multiple times, report only contains last calculation`() {
        val input1 = listOf(
            Rating(Bilateral.LEFT_ARM, AwardPercentage.TWENTY, 1),
            Rating(Bilateral.RIGHT_ARM, AwardPercentage.TWENTY, 1)
        )

        val input2 = listOf(
            Rating(Bilateral.NON_BILATERAL, AwardPercentage.SEVENTY)
        )

        subject.calculateFinalRating(input1)
        subject.calculateFinalRating(input2)

        assertThat(subject.finalReport).containsExactly(
            "Starting New Rating Calculation:",
            "First rating (most severe) = Rating(bilateral=NON_BILATERAL, awardPercentage=SEVENTY, bilateralId=0)",
            "Adding sum of all bilateral factors (0.0)",
            "Rounding from actual rating of 70",
            "To final rating of 70"
        )
    }

    @Test
    fun `calculateFinalRating, real-world rating decision A, returns 90`() {

        val input = listOf(
            Rating(Bilateral.NON_BILATERAL, AwardPercentage.TWENTY),
            Rating(Bilateral.NON_BILATERAL, AwardPercentage.TEN),
            Rating(Bilateral.LEFT_LEG, AwardPercentage.FIFTY, 1),
            Rating(Bilateral.RIGHT_LEG, AwardPercentage.TEN, 1),
            Rating(Bilateral.LEFT_LEG, AwardPercentage.THIRTY, 2),
            Rating(Bilateral.RIGHT_LEG, AwardPercentage.TEN, 2)
        )

        val actual = subject.calculateFinalRating(input)
        subject.printReport()
        assertThat(actual).isEqualTo(90)
    }

    @Test
    fun `calculateFinalRating, real-world rating decision B, returns 90`() {

        val input = listOf(
            Rating(Bilateral.NON_BILATERAL, AwardPercentage.SEVENTY),
            Rating(Bilateral.NON_BILATERAL, AwardPercentage.FIFTY),
            Rating(Bilateral.NON_BILATERAL, AwardPercentage.TWENTY),
            Rating(Bilateral.RIGHT_LEG, AwardPercentage.TWENTY, 1),
            Rating(Bilateral.LEFT_LEG, AwardPercentage.TEN, 1)
        )
        val actual = subject.calculateFinalRating(input)
        subject.printReport()
        assertThat(actual).isEqualTo(90)
    }

    @Test
    fun `calculateBilateralKicker, given pair of ratings in decision B, returns expected`() {
        val left = Rating(Bilateral.RIGHT_LEG, AwardPercentage.TWENTY, 1)
        val right = Rating(Bilateral.LEFT_LEG, AwardPercentage.TEN, 1)

        assertThat(subject.calculateBilateralKicker(left, right)).isEqualTo(2.8)
    }

    @Test
    fun `huntForBilaterals, given all bilaterals in decision A, returns expected`() {
        val input = listOf(
            Rating(Bilateral.LEFT_LEG, AwardPercentage.FIFTY, 1),
            Rating(Bilateral.RIGHT_LEG, AwardPercentage.TEN, 1),
            Rating(Bilateral.LEFT_LEG, AwardPercentage.THIRTY, 2),
            Rating(Bilateral.RIGHT_LEG, AwardPercentage.TEN, 2)
        )

        assertThat(subject.huntForBilaterals(input)).isEqualTo(9.2)
    }

    @Test
    fun `huntForBilaterals, given non-matching IDs, returns zero`() {
        val input = listOf(
            Rating(Bilateral.LEFT_ARM, AwardPercentage.TWENTY, 1),
            Rating(Bilateral.RIGHT_ARM, AwardPercentage.FIFTY, 2)
        )

        assertThat(subject.huntForBilaterals(input)).isEqualTo(0.0)
    }

    @Test
    fun `huntForBilaterals, given IDs equal zero, returns zero`() {
        val input = listOf(
            Rating(Bilateral.LEFT_LEG, AwardPercentage.FIFTY, 0),
            Rating(Bilateral.RIGHT_LEG, AwardPercentage.TEN, 0),
            Rating(Bilateral.LEFT_LEG, AwardPercentage.THIRTY, 0),
            Rating(Bilateral.RIGHT_LEG, AwardPercentage.TEN, 0)
        )

        assertThat(subject.huntForBilaterals(input)).isEqualTo(0.0)
    }
}