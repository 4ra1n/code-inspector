package code.inspector.form;

import code.inspector.core.Application;
import code.inspector.core.Command;
import code.inspector.core.Const;
import code.inspector.core.util.ExecUtil;
import code.inspector.form.module.*;
import code.inspector.log.Log;
import com.formdev.flatlaf.FlatDarkLaf;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;

public class CodeInspector {
    public static CodeInspector instance;
    private JPanel codeInspectorPanel;
    private JPanel selectJarPanel;
    private JButton jarButton;
    private JTextField jarInfoText;
    private JPanel logPanel;
    private JTextArea logArea;
    private JScrollPane logScroll;
    private JPanel modulePanel;
    private JCheckBox importRtJarCheckBox;
    private JCheckBox debugModeCheckBox;
    private JPanel configPanel;
    private JButton doSButton;
    private JButton rceButton;
    private JButton redirectButton;
    private JButton sqlInjectionButton;
    private JButton ssrfButton;
    private JButton startButton;
    private JButton setOutputButton;
    private JTextField outputText;
    private JPanel actionPanel;
    private JLabel jarInfoLabel;
    private JCheckBox dosCheckBox;
    private JCheckBox rceCheckBox;
    private JCheckBox ssrfCheckBox;
    private JCheckBox sqlInjectionCheckBox;
    private JCheckBox redirectCheckBox;
    private JPanel selectModulePanel;
    private JButton deserializationButton;
    private JCheckBox deserializationCheckBox;
    private JCheckBox analyzeAllLibsCheckBox;
    private JButton openResultButton;
    private JPanel outputPanel;
    private static String outputFilePath;
    private static final Command cmd = new Command();
    private static final HashMap<String, Boolean> option = new HashMap<>();

    public JTextArea getLogArea() {
        return this.logArea;
    }

    private void init() {
        initDoSConfig();
        initRCEConfig();
        initRedirectConfig();
        initSQLInjectionConfig();
        initSSRFConfig();
        initDeserializationConfig();
        initAction();
        initCheckBox();
    }

    private void initCheckBox() {
        option.put(Const.DESERIALIZATION_JDK, true);
        option.put(Const.DESERIALIZATION_FASTJSON, true);
        option.put(Const.DESERIALIZATION_JACKSON, true);
        option.put(Const.DESERIALIZATION_SNAKEYAML, true);
        option.put(Const.DESERIALIZATION_HESSIAN, true);
        option.put(Const.DESERIALIZATION_XML_DECODER, true);
        option.put(Const.DOS_FOR_TYPE, true);
        option.put(Const.DOS_ARRAY_TYPE, true);
        option.put(Const.DOS_REGEX_TYPE, true);
        option.put(Const.DOS_LIST_TYPE, true);
        option.put(Const.RCE_RUNTIME_TYPE, true);
        option.put(Const.RCE_PROCESS_TYPE, true);
        option.put(Const.RCE_GROOVY_TYPE, true);
        option.put(Const.RCE_JNDI_TYPE, true);
        option.put(Const.RCE_SP_EL_TYPE, true);
        option.put(Const.REDIRECT_MODEL_AND_VIEW_TYPE, true);
        option.put(Const.REDIRECT_STRING_TYPE, true);
        option.put(Const.REDIRECT_SEND_RESPONSE_TYPE, true);
        option.put(Const.SQL_JDBC_TEMPLATE_EXECUTE, true);
        option.put(Const.SQL_JDBC_TEMPLATE_UPDATE, true);
        option.put(Const.SQL_JDBC_TEMPLATE_QUERY_ANY, true);
        option.put(Const.SQL_EXECUTE_QUERY, true);
        option.put(Const.SQL_EXECUTE_UPDATE, true);
        option.put(Const.SQL_EXECUTE, true);
        option.put(Const.SSRF_JDK_TYPE, true);
        option.put(Const.SSRF_APACHE_TYPE, true);
        option.put(Const.SSRF_SOCKET_TYPE, true);
        option.put(Const.SSRF_OKHTTP_TYPE, true);
    }

    private void initAction() {
        dosCheckBox.setSelected(true);
        deserializationCheckBox.setSelected(true);
        redirectCheckBox.setSelected(true);
        rceCheckBox.setSelected(true);
        sqlInjectionCheckBox.setSelected(true);
        ssrfCheckBox.setSelected(true);
        outputText.setText("result.html");
        outputFilePath = outputText.getText();
        jarButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int option = fileChooser.showOpenDialog(new JFrame());
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                String absPath = file.getAbsolutePath();
                if (!absPath.endsWith(".jar") && !absPath.endsWith(".war")) {
                    JOptionPane.showMessageDialog(codeInspectorPanel, "not jar/war file");
                    return;
                }
                if (!Files.exists(Paths.get(absPath))) {
                    JOptionPane.showMessageDialog(codeInspectorPanel, "file not exist");
                    return;
                }
                cmd.jars.clear();
                cmd.jars.add(absPath);
                jarInfoText.setText(absPath);
            }
        });
        setOutputButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int option = fileChooser.showOpenDialog(new JFrame());
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                String absPath = file.getAbsolutePath();
                if (!Files.exists(Paths.get(absPath))) {
                    JOptionPane.showMessageDialog(codeInspectorPanel, "dir not exist");
                    return;
                }
                cmd.path = String.format("%s%s%s", absPath, File.separator, "result.html");
                outputText.setText(cmd.path);
                outputFilePath = cmd.path;
            }
        });
        openResultButton.addActionListener(e -> {
            if (outputFilePath == null) {
                JOptionPane.showMessageDialog(codeInspectorPanel, "no output file");
                return;
            }
            if (!outputFilePath.trim().equals("")) {
                if (Files.exists(Paths.get(outputFilePath))) {
                    new Thread(() -> ExecUtil.execOpen(outputFilePath)).start();
                } else {
                    JOptionPane.showMessageDialog(codeInspectorPanel, "no output file");
                }
            }
        });
        startButton.addActionListener(e -> {
            option.put(Const.DOS_MODULE, dosCheckBox.isSelected());
            option.put(Const.SQL_MODULE, sqlInjectionCheckBox.isSelected());
            option.put(Const.RCE_MODULE, rceCheckBox.isSelected());
            option.put(Const.SSRF_MODULE, ssrfCheckBox.isSelected());
            option.put(Const.REDIRECT_MODULE, redirectCheckBox.isSelected());
            option.put(Const.DESERIALIZATION_MODULE, deserializationCheckBox.isSelected());
            Log.info("start code-inspector");
            cmd.isDebug = debugModeCheckBox.isSelected();
            cmd.jdk = importRtJarCheckBox.isSelected();
            cmd.lib = analyzeAllLibsCheckBox.isSelected();
            cmd.setOptions(option);
            new Thread(() -> Application.start(cmd, true)).start();
        });
    }

    private void initDeserializationConfig() {
        deserializationButton.addActionListener(e -> {
            JFrame frame = new JFrame("deserialization module");
            frame.setContentPane(new DeserializationModule(option).parentPanel);
            frame.setLocationRelativeTo(codeInspectorPanel);
            frame.pack();
            frame.setVisible(true);
        });
    }

    private void initSSRFConfig() {
        ssrfButton.addActionListener(e -> {
            JFrame frame = new JFrame("ssrf module");
            frame.setContentPane(new SSRFModule(option).parentPanel);
            frame.setLocationRelativeTo(codeInspectorPanel);
            frame.pack();
            frame.setVisible(true);
        });
    }

    private void initSQLInjectionConfig() {
        sqlInjectionButton.addActionListener(e -> {
            JFrame frame = new JFrame("sql injection module");
            frame.setContentPane(new SQLModule(option).parentPanel);
            frame.setLocationRelativeTo(codeInspectorPanel);
            frame.pack();
            frame.setVisible(true);
        });
    }

    private void initRedirectConfig() {
        redirectButton.addActionListener(e -> {
            JFrame frame = new JFrame("redirect module");
            frame.setContentPane(new RedirectModule(option).parentPanel);
            frame.setLocationRelativeTo(codeInspectorPanel);
            frame.pack();
            frame.setVisible(true);
        });
    }

    private void initRCEConfig() {
        rceButton.addActionListener(e -> {
            JFrame frame = new JFrame("rce module");
            frame.setContentPane(new RCEModule(option).parentPanel);
            frame.setLocationRelativeTo(codeInspectorPanel);
            frame.pack();
            frame.setVisible(true);
        });
    }

    private void initDoSConfig() {
        doSButton.addActionListener(e -> {
            JFrame frame = new JFrame("dos module");
            frame.setContentPane(new DoSModule(option).parentPanel);
            frame.setLocationRelativeTo(codeInspectorPanel);
            frame.pack();
            frame.setVisible(true);
        });
    }

    public static void start0() {
        FlatDarkLaf.setup();
        JFrame frame = new JFrame("code-inspector");
        instance = new CodeInspector();
        frame.setContentPane(instance.codeInspectorPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setJMenuBar(createMenuBar());
        frame.pack();
        frame.setVisible(true);
        instance.init();
    }

    private static JMenu createAboutMenu() {
        JMenu aboutMenu = new JMenu("about");
        JMenuItem workItem = new JMenuItem("how does this work");
        workItem.addActionListener(e -> {
            JFrame frame = new JFrame("how does this work");
            frame.setContentPane(new HowThisWork().workPanel);
            frame.setLocationRelativeTo(instance.codeInspectorPanel);
            frame.pack();
            frame.setVisible(true);
        });
        aboutMenu.add(workItem);
        JMenuItem authorItem = new JMenuItem("author");
        authorItem.addActionListener(e -> {
            JFrame frame = new JFrame("author");
            frame.setContentPane(new AuthorForm().authorPanel);
            frame.setLocationRelativeTo(instance.codeInspectorPanel);
            frame.pack();
            frame.setVisible(true);
        });
        aboutMenu.add(authorItem);
        return aboutMenu;
    }

    private static JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createAboutMenu());
        return menuBar;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        codeInspectorPanel = new JPanel();
        codeInspectorPanel.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        selectJarPanel = new JPanel();
        selectJarPanel.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        codeInspectorPanel.add(selectJarPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        selectJarPanel.setBorder(BorderFactory.createTitledBorder(null, "Input", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        jarButton = new JButton();
        jarButton.setText("Select Your Jar / War File");
        selectJarPanel.add(jarButton, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        jarInfoText = new JTextField();
        jarInfoText.setEditable(false);
        selectJarPanel.add(jarInfoText, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 1, false));
        jarInfoLabel = new JLabel();
        jarInfoLabel.setText("Path");
        selectJarPanel.add(jarInfoLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        logPanel = new JPanel();
        logPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        codeInspectorPanel.add(logPanel, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        logPanel.setBorder(BorderFactory.createTitledBorder(null, "Log", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        logScroll = new JScrollPane();
        logPanel.add(logScroll, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(500, 200), null, null, 0, false));
        logArea = new JTextArea();
        logArea.setEditable(false);
        Font logAreaFont = this.$$$getFont$$$("Consolas", -1, -1, logArea.getFont());
        if (logAreaFont != null) logArea.setFont(logAreaFont);
        logArea.setForeground(new Color(-16711895));
        logArea.setText("");
        logScroll.setViewportView(logArea);
        modulePanel = new JPanel();
        modulePanel.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        codeInspectorPanel.add(modulePanel, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        modulePanel.setBorder(BorderFactory.createTitledBorder(null, "Module Config", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        doSButton = new JButton();
        doSButton.setText("DoS");
        modulePanel.add(doSButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        rceButton = new JButton();
        rceButton.setText("RCE");
        modulePanel.add(rceButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        redirectButton = new JButton();
        redirectButton.setText("Redirect");
        modulePanel.add(redirectButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        sqlInjectionButton = new JButton();
        sqlInjectionButton.setText("SQL Injection");
        modulePanel.add(sqlInjectionButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        ssrfButton = new JButton();
        ssrfButton.setText("SSRF");
        modulePanel.add(ssrfButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        deserializationButton = new JButton();
        deserializationButton.setText("Deserialization");
        modulePanel.add(deserializationButton, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        configPanel = new JPanel();
        configPanel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        codeInspectorPanel.add(configPanel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        configPanel.setBorder(BorderFactory.createTitledBorder(null, "Config", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        importRtJarCheckBox = new JCheckBox();
        importRtJarCheckBox.setText("import rt.jar");
        configPanel.add(importRtJarCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        debugModeCheckBox = new JCheckBox();
        debugModeCheckBox.setText("debug mode");
        configPanel.add(debugModeCheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        analyzeAllLibsCheckBox = new JCheckBox();
        analyzeAllLibsCheckBox.setText("analyze all libs");
        configPanel.add(analyzeAllLibsCheckBox, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        actionPanel = new JPanel();
        actionPanel.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        codeInspectorPanel.add(actionPanel, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        actionPanel.setBorder(BorderFactory.createTitledBorder(null, "Action", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        startButton = new JButton();
        startButton.setText("Start");
        actionPanel.add(startButton, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        selectModulePanel = new JPanel();
        selectModulePanel.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        actionPanel.add(selectModulePanel, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        dosCheckBox = new JCheckBox();
        dosCheckBox.setText("DoS");
        selectModulePanel.add(dosCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        rceCheckBox = new JCheckBox();
        rceCheckBox.setText("RCE");
        selectModulePanel.add(rceCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        ssrfCheckBox = new JCheckBox();
        ssrfCheckBox.setText("SSRF");
        selectModulePanel.add(ssrfCheckBox, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        sqlInjectionCheckBox = new JCheckBox();
        sqlInjectionCheckBox.setText("SQL Injection");
        selectModulePanel.add(sqlInjectionCheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        redirectCheckBox = new JCheckBox();
        redirectCheckBox.setText("Redirect");
        selectModulePanel.add(redirectCheckBox, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        deserializationCheckBox = new JCheckBox();
        deserializationCheckBox.setText("Deserialization");
        selectModulePanel.add(deserializationCheckBox, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        outputPanel = new JPanel();
        outputPanel.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        actionPanel.add(outputPanel, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        setOutputButton = new JButton();
        setOutputButton.setText("Set Output");
        outputPanel.add(setOutputButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        outputText = new JTextField();
        outputText.setEditable(false);
        outputPanel.add(outputText, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), new Dimension(150, -1), null, 1, false));
        openResultButton = new JButton();
        openResultButton.setText("Open Result");
        outputPanel.add(openResultButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return codeInspectorPanel;
    }

}
