package com.github.atternatt.powercommit.utils

private fun getSystemProperty(property: String): String? {
  return try {
    System.getProperty(property)
  } catch (ex: SecurityException) {
    // we are not allowed to look at this property
    System.err.println(
      "Caught a SecurityException reading the system property '" + property
        + "'; the SystemUtils property value will default to null."
    )
    null
  }
}