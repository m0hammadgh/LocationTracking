package com.golriz.gpspointer.Config

data class Config(
    var distanceFromPreviousPoint: Double? = 0.0,
    var newPointDelay: Double? = 0.0,
    var syncToServerTime: Int? = 0
)
