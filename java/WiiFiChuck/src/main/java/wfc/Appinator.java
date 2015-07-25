/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wfc;

import javafx.application.Preloader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Lazy;
import wfc.glue.SpringFxmlLoader;
import wfc.glue.SpringJavaFxApplication;
import wfc.viewmodel.Controllinator;

/**
 * @author Thomas Darimont
 */
@Lazy
@SpringBootApplication
public class Appinator extends SpringJavaFxApplication {

    /**
     * Note that this is configured in application.properties
     */
    @Value("${app.ui.title:Example Appinator}")//
    private String windowTitle;

    @Autowired
    private SpringFxmlLoader springFxmlLoader;

    @Override
    public void start(Stage stage) throws Exception {
        notifyPreloader(new Preloader.StateChangeNotification(Preloader.StateChangeNotification.Type.BEFORE_START));

        Parent root = (Parent) springFxmlLoader.load("/wfc/view/Viewinator.fxml", Controllinator.class);
        stage.setTitle(windowTitle);
        stage.setScene(new Scene(root));
        stage.setResizable(true);
        stage.show();
    }

    public static void main(String[] args) {
        launchApp(Appinator.class, args);
    }

}
