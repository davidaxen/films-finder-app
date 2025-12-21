package com.darvi.filmhunter.data.util

object Base32CodeGenerator {
    private const val BASE32_CHARS = "ABCDEFGHJKMNPQRSTUVWXYZ23456789"
    private const val CODE_LENGTH = 6

    /**
     * Generates a Base32 code using only the specified characters
     * Format: 6 characters (e.g., "FH-92KD")
     */
    fun generateCode(): String {
        val random = java.util.Random()
        val code = StringBuilder(CODE_LENGTH)
        
        repeat(CODE_LENGTH) {
            val index = random.nextInt(BASE32_CHARS.length)
            code.append(BASE32_CHARS[index])
        }
        
        return "FH-${code.toString()}"
    }
}

