package com.example.guitartuner.data.tuner

import com.example.guitartuner.domain.repository.tuner.PermissionManager
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PermissionStateTest {

    @Test
    fun permissionState_defaults() {
        val denied = PermissionManager.PermissionState(
            hasRequiredPermissions = false,
            canRequest = true,
        )
        assertFalse(denied.hasRequiredPermissions)
        assertTrue(denied.canRequest)

        val granted = PermissionManager.PermissionState(
            hasRequiredPermissions = true,
            canRequest = false,
        )
        assertTrue(granted.hasRequiredPermissions)
        assertFalse(granted.canRequest)
    }
}
