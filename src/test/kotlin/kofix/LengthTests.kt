package kofix

import org.junit.jupiter.api.RepeatedTest
import kotlin.random.Random
import kotlin.test.assertEquals

class LengthTests {

    @RepeatedTest(10)
    fun `Generates correct amount of instances`() {
        val ints = fixture<Int>().generate(Random).take(20).toList()

        assertEquals(20, ints.size)
    }
}