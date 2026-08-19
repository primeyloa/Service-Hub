package servicehub;

import org.junit.jupiter.api.Test;
import servicehub.ui.ServiceHubGUI;

import java.awt.GraphicsEnvironment;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ServiceHubGUITest {

    @Test
    void guiConstructsWithoutErrors() {
        if (GraphicsEnvironment.isHeadless()) {
            return; // skip when no display is available
        }
        ServiceHubGUI gui = new ServiceHubGUI();
        assertNotNull(gui);
        gui.dispose();
    }
}
