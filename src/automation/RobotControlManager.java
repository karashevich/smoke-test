/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package automation;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.wm.IdeFocusManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sergey Karashevich on 28/01/16.
 */
public class RobotControlManager {

  private RobotControl myRobotControl;
  private Map<String, String> mapping;
  private Component motherComponent;

  private String imagePath = "/Users/jetbrains/IdeaProjects/snapshots/snapshot.png";

  public static RobotControlManager getInstance() {
    return ServiceManager.getService(RobotControlManager.class);
  }

  public RobotControlManager()  {
    myRobotControl = new RobotControl();
    mapping = new HashMap();
    final String SMOKE_TEST_CONFIG_PROJ= "plugin.smoke-test.project";
    String projName = System.getProperty(SMOKE_TEST_CONFIG_PROJ);
    System.out.println(SMOKE_TEST_CONFIG_PROJ + "=" + projName);
    projName = "smoke-test-" + (new SimpleDateFormat("dd-MMM-yy-HH-mm")).format(new Date());

    mapping = new HashMap<>();
    mapping.put("$PROJECT_NAME", projName);
  }

  public Map<String, String> getMapping(){
    return mapping;
  }

  public void setMotherComponent(Component motherComponent) {
    this.motherComponent = motherComponent;
  }

  public Component getMotherComponent() {
    return motherComponent;
  }


  public RobotControl getRobotControl() {
    return myRobotControl;
  }


  public String getImagePath() {
    return imagePath;
  }

  public static void main(String[] args) {
    RobotControl myRobotControl = new RobotControl();
    myRobotControl.startRobotActivity();
    try {
      myRobotControl.mouseMove(new Point(800, 600));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
