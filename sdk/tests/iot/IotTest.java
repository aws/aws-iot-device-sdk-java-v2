import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assertions.assertTrue;

import software.amazon.awssdk.crt.CRT;


public class IotTest {
    @Test
    public void testCRTIsFIPS() {
        assumeTrue(System.getenv("CRT_FIPS") != null);
        assertTrue(CRT.isFIPS());
    }
}
