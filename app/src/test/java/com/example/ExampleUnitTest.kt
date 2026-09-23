package com.example

import com.example.data.auth.FirebaseAuthManager
import com.example.data.model.UserRole
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  private val authManager = FirebaseAuthManager()

  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testStudentDomainRole() {
    val role = authManager.determineRole("samuel.cs@karunya.edu.in")
    assertEquals(UserRole.STUDENT, role)
  }

  @Test
  fun testCoordinatorDomainRole() {
    val role = authManager.determineRole("johnpeter@karunya.edu")
    assertEquals(UserRole.COORDINATOR, role)
  }

  @Test
  fun testDriverGmailDomainRole() {
    val role = authManager.determineRole("murugan.transport@gmail.com")
    assertEquals(UserRole.DRIVER, role)
  }

  @Test
  fun testAdminDomainRole() {
    val role1 = authManager.determineRole("admin@karunya.edu")
    assertEquals(UserRole.TRANSPORT_ADMIN, role1)

    val role2 = authManager.determineRole("transport@karunya.edu")
    assertEquals(UserRole.TRANSPORT_ADMIN, role2)
  }
}
