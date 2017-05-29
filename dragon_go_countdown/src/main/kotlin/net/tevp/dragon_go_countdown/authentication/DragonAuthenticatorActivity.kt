package net.tevp.dragon_go_countdown.authentication

import android.accounts.Account
import android.accounts.AccountAuthenticatorActivity
import android.accounts.AccountManager
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import net.tevp.dragon_go_countdown.DragonServer
import net.tevp.dragon_go_countdown.R

/**
 * A login screen that offers login via username/password.
 */
class DragonAuthenticatorActivity : AccountAuthenticatorActivity() {


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private var mAuthTask: UserLoginTask? = null

    // UI references.
    private var mUsernameView: EditText? = null
    private var mPasswordView: EditText? = null
    private var mProgressView: View? = null
    private var mLoginFormView: View? = null
    private var mAccountManager: AccountManager? = null
    private var mAuthTokenType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dragon_login)

        mAccountManager = AccountManager.get(baseContext)

        mAuthTokenType = intent.getStringExtra(ARG_AUTH_TYPE)
        if (mAuthTokenType == null)
            mAuthTokenType = AUTHTOKEN_TYPE_FULL_ACCESS

        // Set up the login form.
        mUsernameView = findViewById(R.id.username) as EditText
        val accountName = intent.getStringExtra(ARG_ACCOUNT_NAME)
        if (accountName != null) {
            mUsernameView!!.setText(accountName)
        }

        mPasswordView = findViewById(R.id.password) as EditText
        mPasswordView!!.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        val mUsernameSignInButton = findViewById(R.id.sign_in_button) as Button
        mUsernameSignInButton.setOnClickListener { attemptLogin() }

        mLoginFormView = findViewById(R.id.login_form)
        mProgressView = findViewById(R.id.login_progress)
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    fun attemptLogin() {
        if (mAuthTask != null) {
            return
        }

        // Reset errors.
        mUsernameView!!.error = null
        mPasswordView!!.error = null

        // Store values at the time of the login attempt.
        val username = mUsernameView!!.text.toString()
        val password = mPasswordView!!.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView!!.error = getString(R.string.error_incorrect_password)
            focusView = mPasswordView
            cancel = true
        }

        // Check for a valid username
        if (TextUtils.isEmpty(username)) {
            mUsernameView!!.error = getString(R.string.error_field_required)
            focusView = mUsernameView
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView!!.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true)
            mAuthTask = UserLoginTask(username, password)
            mAuthTask!!.execute()
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime)

            mLoginFormView!!.visibility = if (show) View.GONE else View.VISIBLE
            mLoginFormView!!.animate().setDuration(shortAnimTime.toLong()).alpha(
                    (if (show) 0 else 1).toFloat()).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    mLoginFormView!!.visibility = if (show) View.GONE else View.VISIBLE
                }
            })

            mProgressView!!.visibility = if (show) View.VISIBLE else View.GONE
            mProgressView!!.animate().setDuration(shortAnimTime.toLong()).alpha(
                    (if (show) 1 else 0).toFloat()).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    mProgressView!!.visibility = if (show) View.VISIBLE else View.GONE
                }
            })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView!!.visibility = if (show) View.VISIBLE else View.GONE
            mLoginFormView!!.visibility = if (show) View.GONE else View.VISIBLE
        }
    }

    private fun finishLogin(intent: Intent) {
        val accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
        val accountPassword = intent.getStringExtra(PARAM_USER_PASS)
        val account = Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE))

        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            val authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN)
            val authtokenType = mAuthTokenType

            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            mAccountManager!!.addAccountExplicitly(account, accountPassword, intent.getBundleExtra(AccountManager.KEY_USERDATA))
            mAccountManager!!.setAuthToken(account, authtokenType, authtoken)
        } else {
            mAccountManager!!.setPassword(account, accountPassword)
        }

        setAccountAuthenticatorResult(intent.extras)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    inner class UserLoginTask internal constructor(private val mUsername: String, private val mPassword: String) : AsyncTask<Void, Void, Intent>() {
        private var loginResult: LoginResult? = null

        internal val accountType = intent.getStringExtra(ARG_ACCOUNT_TYPE)

        override fun doInBackground(vararg params: Void): Intent {
            val data = Bundle()
            try {
                loginResult = DragonServer.Login(mUsername, mPassword)
                if (loginResult!!.status == LoginStatus.SUCCESS) {
                    data.putString(AccountManager.KEY_ACCOUNT_NAME, mUsername)
                    data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType)
                    data.putString(AccountManager.KEY_AUTHTOKEN, loginResult!!.sessionCode)
                    data.putString(PARAM_USER_PASS, mPassword)
                }

            } catch (e: Exception) {
                data.putString(KEY_ERROR_MESSAGE, e.message)
            }

            val res = Intent()
            res.putExtras(data)
            return res
        }

        override fun onPostExecute(intent: Intent) {
            mAuthTask = null
            showProgress(false)

            when (loginResult!!.status) {
                LoginStatus.BAD_PASSWORD -> {
                    mPasswordView!!.error = getString(R.string.error_incorrect_password)
                    mPasswordView!!.requestFocus()
                }
                LoginStatus.BAD_USERNAME -> {
                    mUsernameView!!.error = getString(R.string.error_invalid_username)
                    mUsernameView!!.requestFocus()
                }
                LoginStatus.OTHER_ERROR -> {
                    if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                        Toast.makeText(baseContext, intent.getStringExtra(KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                    mUsernameView!!.error = "Some other problems"
                }
                LoginStatus.SUCCESS -> finishLogin(intent)
            }
        }

        override fun onCancelled() {
            mAuthTask = null
            showProgress(false)
        }
    }

    companion object {
        val ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE"
        val ARG_AUTH_TYPE = "AUTH_TYPE"
        val ARG_ACCOUNT_NAME = "ACCOUNT_NAME"
        val ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT"
        val PARAM_USER_PASS = "USER_PASS"
        val KEY_ERROR_MESSAGE = "ERR_MSG"
        val ACCOUNT_TYPE = "net.tevp.dragon_go_countdown"
        val AUTHTOKEN_TYPE_FULL_ACCESS = "Full access"
    }
}



