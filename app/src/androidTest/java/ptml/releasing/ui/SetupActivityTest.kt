package ptml.releasing.ui

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ptml.releasing.ui.setup.SetupActivity


@RunWith(AndroidJUnit4::class)
class SetupActivityTest{
    @Rule @JvmField
    var permissionRule = GrantPermissionRule.grant(android.Manifest.permission.READ_PHONE_STATE)

    @Rule @JvmField
    var activityRule = ActivityTestRule(SetupActivity::class.java)


    @Test
    fun verifyDeviceId(){

    }



}