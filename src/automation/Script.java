package automation;

import automation.commands.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.util.containers.Queue;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by jetbrains on 28/03/16.
 */
public class Script extends AnAction {

    private String name;
    private Document doc;
    private Queue<Command> queueOfCommands;
    @Nullable
    private Map<String, String> mapping;

    public Script(String name, @Nullable Map<String, String> mapping) throws JDOMException, IOException {
        super("automate." + name);
        this.name = name;
        this.mapping = mapping;

        URL resource = this.getClass().getClassLoader().getResource("/automation/scripts/");
        File file1 = new File(resource.getFile() + name + ".xml");
        InputStream inputStream = new FileInputStream(file1);

        SAXBuilder builder = new SAXBuilder();
        doc = builder.build(inputStream);

    }

    private void buildQueue() throws Exception {
        Element root = doc.getRootElement();
        List<Element> children = root.getChildren();
        if (queueOfCommands == null) {
            queueOfCommands = new Queue<>(children.size());
        } else {
            queueOfCommands.clear();
        }

        for (Element element : children) {
            Command command = null;
            switch (element.getName()) {
                case "start":
                    command = new StartCommand();
                    break;
                case "navigate-and-click":
                    command = new NavigateAndClickCommand(getParam(element));
                    break;
                case "navigate-and-click-menu":
                    command = new NavigateAndClickMenuCommand(getParam(element));
                    break;
                case "wait-dialog-command":
                    command = new WaitDialogCommand(getParam(element));
                    break;
                case "select-in-list":
                    command = new SelectInListCommand(getParam(element));
                    break;
                case "select-framework":
                    command = new SelectInFrameworkListCommand(getParam(element));
                    break;
                case "select-in-JDK-list":
                    command = new SelectInJdkListCommand(getParam(element));
                    break;
                case "show-tool-windows":
                    command = new ShowToolWindowsCommand();
                    break;
                case "type-text-in-text-field":
                    command = new TypeInTextFieldCommand(getTypedText(element));
                    break;
                case "wait":
                    command = new WaitCommand(getDelay(element));
                    break;
                case "wait-ui":
                    command = new WaitUiCommand(getParam(element));
                    break;
                case "project-view":
                    command = new ProjectViewCommand(element);
                    break;
                case "wait-project-open":
                    command = new WaitProjectOpeningCommand(new Parameters());
                    break;
                case "park-mouse":
                    command = new ParkMouseCommand(element);
                    break;
                case "print":
                    command = getPrintCommand(element);
                    break;

            }
            if (command != null) queueOfCommands.addLast(command);
            else
                throw new Exception("Unable to build command from xml-tag:" + element.getName() + ". Check your script!");
        }
    }

    private Parameters getParam(Element element) throws Exception {
        final String paramStr = "param";
        if (element.getAttribute(paramStr) != null) {
            String params = element.getAttributeValue(paramStr);
            return new Parameters(res(params));
        } else throw new Exception("Unable to get param from Element: " + element);
    }

    private Parameters getTypedText(Element element) throws Exception {
        String location = "";
        final String locationStr = "location";
        if (element.getAttribute(locationStr) != null)
            location = element.getAttributeValue(locationStr);

        final String typeTextStr = "type-text";
        if (element.getAttribute(typeTextStr) != null) {
            String params = element.getAttributeValue(typeTextStr);
            return new Parameters(res(location), null, res(params));
        } else throw new Exception("Unable to get type-text parameter from Element: " + element);
    }

    private Parameters getDelay(Element element) throws Exception {
        final String delayStr = "delay";
        if (element.getAttribute(delayStr) != null)
            return new Parameters(Integer.parseInt(element.getAttributeValue(delayStr)));
        else throw new Exception("Unable to get delay parameter from Element: " + element);
    }

    private Command getPrintCommand(Element element) throws Exception {
        String value = element.getValue();
        return new RunnableCommand(new Runnable() {
            @Override
            public void run() {
                System.out.println(value);
            }
        });
    }

    //resolve mapping. Example: map("$PROJECT_FILE") -> "smoke-text/idea" will substitute $PROJECT_FILE with smoke-text/.idea
    private String res(String param) {
        if (mapping != null) if (mapping.containsKey(param)) return mapping.get(param);
        return param;
    }

    void process() throws Exception {
        queueOfCommands.pullFirst().process(queueOfCommands);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        try {
            buildQueue();
            this.process();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
