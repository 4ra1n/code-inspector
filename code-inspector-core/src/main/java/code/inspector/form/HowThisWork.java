package code.inspector.form;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class HowThisWork {
    public JPanel workPanel;
    private JLabel firstLabel;
    private JLabel secondLabel;
    private JLabel thirdLabel;
    private JLabel fourthLabel;
    private JLabel fifthLabel;
    private JLabel sixthLabel;
    private JLabel seventhLabel;
    private JLabel eighthLabel;
    private JLabel ninthLabel;

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
        workPanel = new JPanel();
        workPanel.setLayout(new GridLayoutManager(9, 1, new Insets(0, 0, 0, 0), -1, -1));
        workPanel.setBorder(BorderFactory.createTitledBorder(null, "steps", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        firstLabel = new JLabel();
        firstLabel.setText("1. unzip jar and parse class files");
        workPanel.add(firstLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        secondLabel = new JLabel();
        secondLabel.setText("2. discover all classes and metdhos");
        workPanel.add(secondLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        thirdLabel = new JLabel();
        thirdLabel.setText("3. build inheritance between classes");
        workPanel.add(thirdLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        fourthLabel = new JLabel();
        fourthLabel.setText("4. find all method call in each methods");
        workPanel.add(fourthLabel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        fifthLabel = new JLabel();
        fifthLabel.setText("5. topological sort methods");
        workPanel.add(fifthLabel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        sixthLabel = new JLabel();
        sixthLabel.setText("6. get args taint between method calls");
        workPanel.add(sixthLabel, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        seventhLabel = new JLabel();
        seventhLabel.setText("7. parse spring mapping and set it to the source   ");
        workPanel.add(seventhLabel, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        eighthLabel = new JLabel();
        eighthLabel.setText("8. start vulnerability scan (some modules)");
        workPanel.add(eighthLabel, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        ninthLabel = new JLabel();
        ninthLabel.setText("9. simulate jvm stack frame for taint analysis");
        workPanel.add(ninthLabel, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return workPanel;
    }

}
