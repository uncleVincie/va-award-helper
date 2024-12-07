package com.vincie.model

import com.vincie.com.vincie.model.AwardPercentage
import com.vincie.com.vincie.model.CombinedRatingsTable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CombinedRatingsTableTest {

    private val subject = CombinedRatingsTable()

    @ParameterizedTest
    @MethodSource("combineRatingsSource")
    fun `combineRating, given valid inputs, returns expected`(currentRating: Int, awardPercentage: AwardPercentage, expected: Int) {
        assertThat(subject.combineRating(currentRating, awardPercentage)).isEqualTo(expected)
        //TODO parameterize and test several combos, at least one for each map
    }

    @Test
    fun `combineRating, given bad currentRating, returns null`() {
        assertThat(subject.combineRating(101, AwardPercentage.TEN)).isNull()
    }

    private fun combineRatingsSource(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(26,AwardPercentage.FORTY,56),
            Arguments.of(19,AwardPercentage.TEN,27),
            Arguments.of(20,AwardPercentage.TWENTY,36),
            Arguments.of(33,AwardPercentage.THIRTY,53),
            Arguments.of(44,AwardPercentage.FORTY,66),
            Arguments.of(51,AwardPercentage.FIFTY,76),
            Arguments.of(67,AwardPercentage.SIXTY,87),
            Arguments.of(75,AwardPercentage.SEVENTY,93),
            Arguments.of(82,AwardPercentage.EIGHTY,96),
            Arguments.of(94,AwardPercentage.NINETY,99),
            Arguments.of(94,AwardPercentage.TEN,95),
            Arguments.of(88,AwardPercentage.TWENTY,90),
            Arguments.of(73,AwardPercentage.THIRTY,81),
            Arguments.of(65,AwardPercentage.FORTY,79),
            Arguments.of(56,AwardPercentage.FIFTY,78),
            Arguments.of(47,AwardPercentage.SIXTY,79),
            Arguments.of(38,AwardPercentage.SEVENTY,81),
            Arguments.of(25,AwardPercentage.EIGHTY,85),
            Arguments.of(19,AwardPercentage.NINETY,92)
        )
    }
}