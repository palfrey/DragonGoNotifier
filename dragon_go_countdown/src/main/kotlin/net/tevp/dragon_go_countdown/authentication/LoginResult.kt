package net.tevp.dragon_go_countdown.authentication

import java.util.*

class LoginResult {
    var status: LoginStatus = LoginStatus.UNKNOWN
    var sessionCode: String? = null
    var expiry: Date? = null
}
