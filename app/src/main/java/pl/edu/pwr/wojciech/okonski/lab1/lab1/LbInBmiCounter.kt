package pl.edu.pwr.wojciech.okonski.lab1.lab1

class LbInBmiCounter : BmiCounter {
    private val minimalMass = 22.05f
    private val maximalMass = 551.16f
    private val minimalHeight = 19.69f
    private val maximalHeight = 98.43f

    override fun isMassValid(mass: Float): Boolean {
        return mass > minimalMass && mass < maximalMass
    }

    override fun isHeightValid(height: Float): Boolean {
        return height > minimalHeight && height < maximalHeight
    }

    override fun calculateBMI(mass: Float, height: Float): Float {
        if (!isMassValid(mass) || !isHeightValid(height))
            throw IllegalArgumentException()
        val bmi = mass / (height * height) * 703
        return Math.round(bmi * 100f) / 100f
    }
}