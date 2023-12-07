import java.security.MessageDigest

private val digest = MessageDigest.getInstance("SHA-1")

class Hasher {

    companion object {

        fun getHashCode(key: String): String{
            return digest.digest(key.toByteArray()).toString()
        }
    }
}