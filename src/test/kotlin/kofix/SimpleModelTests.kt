package kofix

import kofix.model.SimpleModel
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SimpleModelTests {

    @Test
    fun `Can generate SimpleModel without overrides`() {
        assertDoesNotThrow { fixture<SimpleModel>().generate(Random).first() }
    }

    @RepeatedTest(10)
    fun `Can generate SimpleModel with overrides`() {
        val model = assertDoesNotThrow { fixture<SimpleModel>()
            .set(SimpleModel::age, Int.generator(0, 10))
            .set(SimpleModel::name, String.generator(1, 1, listOf('a')))
            .generate(Random).first()
        }

        assertTrue { model.age >= 0 }
        assertTrue { model.age < 10 }
        assertEquals("a", model.name)
    }
}