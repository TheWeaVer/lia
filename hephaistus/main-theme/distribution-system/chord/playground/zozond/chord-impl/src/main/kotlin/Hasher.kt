import java.security.MessageDigest

class Hasher {

    companion object {
        private val digest = MessageDigest.getInstance("SHA-1")

        fun getHashCode(key: String): String{
            return digest.digest(key.toByteArray()).toString()
        }
    }
}