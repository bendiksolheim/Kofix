package faker

import org.junit.jupiter.api.RepeatedTest
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class BasicTypesTests {

    @Test
    fun `Can generate Ints`() {
        val int = fake<Int>().generate(Random).first()
        val uint = fake<UInt>().generate(Random).first()

        assertIs<Int>(int)
        assertIs<UInt>(uint)
    }

    @Test
    fun `Can generate Shorts`() {
        val short = fake<Short>().generate(Random).first()
        val ushort = fake<UShort>().generate(Random).first()

        assertIs<Short>(short)
        assertIs<UShort>(ushort)
    }

    @Test
    fun `Can generate Bytes`() {
        val byte = fake<Byte>().generate(Random).first()
        val uByte = fake<UByte>().generate(Random).first()

        assertIs<Byte>(byte)
        assertIs<UByte>(uByte)
    }

    @Test
    fun `Can generate Longs`() {
        val long = fake<Long>().generate(Random).first()
        val uLong = fake<ULong>().generate(Random).first()

        assertIs<Long>(long)
        assertIs<ULong>(uLong)
    }

    @Test
    fun `Can generate Floats`() {
        val float = fake<Float>().generate(Random).first()

        assertIs<Float>(float)
    }

    @Test
    fun `Can generate Doubles`() {
        val double = fake<Double>().generate(Random).first()

        assertIs<Double>(double)
    }

    @RepeatedTest(10)
    fun `Can generate Strings`() {
        val s = fake<String>().generate(Random).first()

        assertIs<String>(s)
        assertTrue { s.length <= 20}
    }

    @Test
    fun `Can generate Chars`() {
        val char = fake<Char>().generate(Random).first()

        assertIs<Char>(char)
    }

    @Test
    fun `Can generate Booleans`() {
        val bool = fake<Boolean>().generate(Random).first()

        assertIs<Boolean>(bool)
    }
}