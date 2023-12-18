package interfaces

interface Chord {
    fun findSuccessor(keyId: Int): Chord
}
