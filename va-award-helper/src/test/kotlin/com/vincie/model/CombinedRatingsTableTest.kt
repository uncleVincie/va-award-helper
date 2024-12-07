package com.vincie.model

import com.vincie.com.vincie.model.AwardPercentage
import com.vincie.com.vincie.model.CombinedRatingsTable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CombinedRatingsTableTest {

    private val subject = CombinedRatingsTable()

    @Test
    fun `combineRating, given valid inputs, returns expected`() {
        assertThat(subject.combineRating(26, AwardPercentage.FORTY)).isEqualTo(56)
        //TODO parameterize and test several combos, at least one for each map
    }

    @Test
    fun `combineRating, given bad currentRating, returns null`() {
        assertThat(subject.combineRating(101, AwardPercentage.TEN)).isNull()
    }
}