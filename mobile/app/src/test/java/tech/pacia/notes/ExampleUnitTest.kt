package tech.pacia.notes

import kotlinx.datetime.Clock
import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
        val instant = Clock.System.now()
        val dateSt = instant.toString()
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US)
        val formattedDate = dateFormatter.parse(dateSt).toInstant()

        println(
            DateTimeFormatter.ofPattern("MMMM dd, yyyy | hh:mma")
                .withZone(ZoneId.of("UTC"))
                .format(formattedDate),
        )
    }
}
