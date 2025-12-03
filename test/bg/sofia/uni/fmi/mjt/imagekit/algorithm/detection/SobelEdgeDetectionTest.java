package bg.sofia.uni.fmi.mjt.imagekit.algorithm.detection;

import bg.sofia.uni.fmi.mjt.imagekit.algorithm.ImageAlgorithm;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SobelEdgeDetectionTest {

    @Mock
    private ImageAlgorithm imageAlgorithm;

    @InjectMocks
    private SobelEdgeDetection sobelEdgeDetection;


    @Test
    void testNullInputSobelAlg() {
        assertThrows(IllegalArgumentException.class, () -> sobelEdgeDetection.process(null),
                "Null cannot be passed to sobel algorithm");
    }

}
