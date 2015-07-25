package wfc.glue;

import javafx.fxml.FXMLLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class SpringFxmlLoader {
    private ApplicationContext context;

    @Autowired
    public SpringFxmlLoader(ApplicationContext context) {
        this.context = context;
    }

    public Object load(String url, Class<?> controllerClass) throws IOException {
        InputStream fxmlStream = null;
        try {
            fxmlStream = controllerClass.getResourceAsStream(url);
            Object instance = context.getBean(controllerClass);
            FXMLLoader loader = new FXMLLoader();
            loader.setController(instance);
            loader.getNamespace().put("scope", instance);
            return loader.load(fxmlStream);
        } finally {
            if (fxmlStream != null) {
                fxmlStream.close();
            }
        }
    }
}