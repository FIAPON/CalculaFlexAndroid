package br.com.heiderlopes.calculaflex.utils.featuretoggle

data class FeatureConfig(
    val releasedVersion: Int,
    val minimumVersion: Int,
    val status: FeatureToggleState
)