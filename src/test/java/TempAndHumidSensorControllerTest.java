/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.imos.pi.th.TempAndHumidSensorController;
import java.io.IOException;
import org.junit.Test;

/**
 *
 * @author Pintu
 */
public class TempAndHumidSensorControllerTest {

    Injector injector = Guice.createInjector(new SampleModule());
    TempAndHumidSensorController controller = injector.getInstance(TempAndHumidSensorController.class);

    @Test
    public void test() throws IOException {
        controller.setBaseFolder(".");
        controller.saveDataAsJSON();
    }
}
