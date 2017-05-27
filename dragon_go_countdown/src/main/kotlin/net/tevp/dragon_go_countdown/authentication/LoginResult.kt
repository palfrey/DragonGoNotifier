package net.tevp.dragon_go_countdown.authentication

import java.util.Date

class LoginResult {
    var status: LoginStatus? = null
    var sessionCode: String? = null
    var expiry: Date? = null
}
