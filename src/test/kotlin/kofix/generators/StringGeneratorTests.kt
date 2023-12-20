package kofix.generators

import kofix.generator
import org.junit.jupiter.api.RepeatedTest
import kotlin.random.Random
import kotlin.test.assertEquals

class StringGeneratorTests {

    @RepeatedTest(10)
    fun `Specifying lower and upper bounds as equal should always generate same length`() {
        val s = String.generator(1, 1, listOf('a'))(Random).first()

        assertEquals("a", s)
    }
}