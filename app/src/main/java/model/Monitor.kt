package model

data class Monitor(
    val siteName: String,
    val siteTag: String,
    val deviceId: String,
    var lastUpdated: String,
    var doValue: String,
    var doStatus: String,
    var phValue: String,
    var phStatus: String,
    var tempValue: String,
    var tempStatus: String
)