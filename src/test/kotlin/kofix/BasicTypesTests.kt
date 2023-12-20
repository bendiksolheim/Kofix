package kofix

import org.junit.jupiter.api.RepeatedTest
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class BasicTypesTests {

    @Test
    fun `Can generate Ints`() {
        val int = fixture<Int>().generate(Random).first()
        val uint = fixture<UInt>().generate(Random).first()

        assertIs<Int>(int)
        assertIs<UInt>(uint)
    }

    @Test
    fun `Can generate Shorts`() {
        val short = fixture<Short>().generate(Random).first()
        val ushort = fixture<UShort>().generate(Random).first()

        assertIs<Short>(short)
        assertIs<UShort>(ushort)
    }

    @Test
    fun `Can generate Bytes`() {
        val byte = fixture<Byte>().generate(Random).first()
        val uByte = fixture<UByte>().generate(Random).first()

        assertIs<Byte>(byte)
        assertIs<UByte>(uByte)
    }

    @Test
    fun `Can generate Longs`() {
        val long = fixture<Long>().generate(Random).first()
        val uLong = fixture<ULong>().generate(Random).first()

        assertIs<Long>(long)
        assertIs<ULong>(uLong)
    }

    @Test
    fun `Can generate Floats`() {
        val float = fixture<Float>().generate(Random).first()

        assertIs<Float>(float)
    }

    @Test
    fun `Can generate Doubles`() {
        val double = fixture<Double>().generate(Random).first()

        assertIs<Double>(double)
    }

    @RepeatedTest(10)
    fun `Can generate Strings`() {
        val s = fixture<String>().generate(Random).first()

        assertIs<String>(s)
        assertTrue("[$s] is ${s.length} long") { s.length <= 20}
    }

    @Test
    fun `Can generate Chars`() {
        val char = fixture<Char>().generate(Random).first()

        assertIs<Char>(char)
    }

    @Test
    fun `Can generate Booleans`() {
        val bool = fixture<Boolean>().generate(Random).first()

        assertIs<Boolean>(bool)
    }
}