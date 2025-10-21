package com.coindepo.domain.entities.stats.tier

import korlibs.bignumber.BigNum

data class UserTier(
    val coinDepoTokenPercentage: BigNum,
    val tierId: Int?
)