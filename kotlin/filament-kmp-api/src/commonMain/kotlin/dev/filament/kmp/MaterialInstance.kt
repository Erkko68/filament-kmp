package dev.filament.kmp

expect class MaterialInstance {
    fun getName(): String
    fun setParameter(name: String, x: Float)
    fun setParameter(name: String, x: Float, y: Float)
    fun setParameter(name: String, x: Float, y: Float, z: Float)
    fun setParameter(name: String, x: Float, y: Float, z: Float, w: Float)
    fun setParameter(name: String, x: Int)
    fun setParameter(name: String, x: Boolean)
}
