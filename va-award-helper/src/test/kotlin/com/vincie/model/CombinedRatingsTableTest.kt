package com.vincie.model

import com.vincie.com.vincie.model.CombinedRatingsTable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CombinedRatingsTableTest {

    private val subject = CombinedRatingsTable()

    @Test
    fun `combineRating, given valid inputs, returns expected`() {
        assertThat(subject.combineRating(26, 40)).isEqualTo(56)
    }
}