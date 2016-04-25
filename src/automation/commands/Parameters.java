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
package automation.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sergey Karashevich on 29/01/16.
 */
public class Parameters {

    @Override
  public String toString() {
    return "Parameters{" +
            "textField='" + textField + '\'' +
            ", component=" + component +
            ", myTypedText='" + myTypedText + '\'' +
            ", myTimeout=" + myTimeout +
            '}';
  }

  private String textField;
  private Component component;
  private String myTypedText;
  private int myTimeout = 1000000;

  public int getMyTimeout() {
    return myTimeout;
  }

  public Parameters() {
  }

  public Parameters(int i) {
    myTimeout = i;
  }
  public Parameters(String text) {
    this.textField = text;
  }

  public Parameters(String text, int timeout) {
    this.textField = text;
    this.myTimeout = timeout;
  }

  public Parameters(Component component) {
    this.component = component;
  }

  public Parameters(String textField, Component component) {
    this.textField = textField;
    this.component = component;
  }

  public Parameters(String textField, @Nullable Component component, @NotNull String typedText) {
    this.textField = textField;
    this.component = component;
    this.myTypedText = typedText;
  }

  public void log(){
    System.out.println("    " + (new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS")).format(new Date()) + "----PARAMETERS: " + toString());
  }

  public String getTextField() {
    return textField;
  }

  public Component getComponent() {
    return component;
  }

  public String getTypedText() {
    return myTypedText;
  }
}
