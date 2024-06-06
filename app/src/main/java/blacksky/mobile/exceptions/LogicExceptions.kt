package blacksky.mobile.exceptions

data class BadCredentialsException(override val message: String) : Exception()

data class NotAuthenticatedException(override val message: String) : Exception()