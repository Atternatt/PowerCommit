package com.github.atternatt.powercommit.feature.failue

sealed interface DomainFailure

object NotFound: DomainFailure
object DataEmpty: DomainFailure
object UnknownFailure: DomainFailure
