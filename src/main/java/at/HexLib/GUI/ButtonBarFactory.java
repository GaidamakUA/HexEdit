package at.HexLib.GUI;

import com.jgoodies.forms.builder.ButtonBarBuilder;

import javax.swing.*;

/**
 * A factory class that consists only of static methods to build frequently used
 * button bars. Utilizes the {@link com.jgoodies.forms.builder.ButtonBarBuilder}
 * that in turn uses the {@link com.jgoodies.forms.layout.FormLayout}
 * to lay out the bars.<p>
 * <p>
 * The button bars returned by this builder comply with popular UI style guides.
 *
 * @author Karsten Lentzsch
 * @version $Revision: 1.5 $
 * @see com.jgoodies.forms.builder.ButtonBarBuilder
 * @see com.jgoodies.forms.util.LayoutStyle
 */
public final class ButtonBarFactory {


    private ButtonBarFactory() {
        // Suppresses default constructor, ensuring non-instantiability.
    }


    // General Purpose Factory Methods: Left Aligned ************************

    /**
     * Builds and returns a left aligned bar with one button.
     *
     * @param button1 the first button to add
     * @return a button bar with the given button
     */
    public static JPanel buildLeftAlignedBar(JButton button1) {
        return buildLeftAlignedBar(new JButton[]{
                button1
        });
    }


    /**
     * Builds and returns a left aligned bar with two buttons.
     *
     * @param button1 the first button to add
     * @param button2 the second button to add
     * @return a button bar with the given buttons
     */
    public static JPanel buildLeftAlignedBar(
            JButton button1, JButton button2) {
        return buildLeftAlignedBar(new JButton[]{
                        button1, button2
                },
                true);
    }


    /**
     * Builds and returns a left aligned bar with three buttons.
     *
     * @param button1 the first button to add
     * @param button2 the second button to add
     * @param button3 the third button to add
     * @return a button bar with the given buttons
     */
    public static JPanel buildLeftAlignedBar(
            JButton button1, JButton button2, JButton button3) {
        return buildLeftAlignedBar(new JButton[]{
                        button1, button2, button3
                },
                true);
    }


    /**
     * Builds and returns a left aligned bar with four buttons.
     *
     * @param button1 the first button to add
     * @param button2 the second button to add
     * @param button3 the third button to add
     * @param button4 the fourth button to add
     * @return a button bar with the given buttons
     */
    public static JPanel buildLeftAlignedBar(
            JButton button1, JButton button2, JButton button3, JButton button4) {
        return buildLeftAlignedBar(new JButton[]{
                        button1, button2, button3, button4
                },
                true);
    }


    /**
     * Builds and returns a left aligned bar with five buttons.
     *
     * @param button1 the first button to add
     * @param button2 the second button to add
     * @param button3 the third button to add
     * @param button4 the fourth button to add
     * @param button5 the fifth button to add
     * @return a button bar with the given buttons
     */
    public static JPanel buildLeftAlignedBar(
            JButton button1, JButton button2, JButton button3,
            JButton button4, JButton button5) {
        return buildLeftAlignedBar(new JButton[]{
                        button1, button2, button3, button4, button5
                },
                true);
    }


    /**
     * Builds and returns a left aligned button bar with the given buttons.
     *
     * @param buttons an array of buttons to add
     * @return a left aligned button bar with the given buttons
     */
    public static JPanel buildLeftAlignedBar(JButton[] buttons) {
        ButtonBarBuilder builder = new ButtonBarBuilder();
        builder.addButton(buttons);
        builder.addGlue();
        return builder.getPanel();
    }


    /**
     * Builds and returns a left aligned button bar with the given buttons.
     *
     * @param buttons                an array of buttons to add
     * @param leftToRightButtonOrder the order in which the buttons to add
     * @return a left aligned button bar with the given buttons
     */
    public static JPanel buildLeftAlignedBar(
            JButton[] buttons,
            boolean leftToRightButtonOrder) {
        ButtonBarBuilder builder = new ButtonBarBuilder();
        builder.setLeftToRight(leftToRightButtonOrder);
        builder.addButton(buttons);
        builder.addGlue();
        return builder.getPanel();
    }


    // General Purpose Factory Methods: Centered ****************************

    /**
     * Builds and returns a centered bar with one button.
     *
     * @param button1 the first button to add
     * @return a button bar with the given button
     */
    public static JPanel buildCenteredBar(JButton button1) {
        return buildCenteredBar(new JButton[]{
                button1
        });
    }


    /**
     * Builds and returns a centered bar with two buttons.
     *
     * @param button1 the first button to add
     * @param button2 the second button to add
     * @return a button bar with the given buttons
     */
    public static JPanel buildCenteredBar(
            JButton button1, JButton button2) {
        return buildCenteredBar(new JButton[]{
                button1, button2
        });
    }


    /**
     * Builds and returns a centered bar with three buttons.
     *
     * @param button1 the first button to add
     * @param button2 the second button to add
     * @param button3 the third button to add
     * @return a button bar with the given buttons
     */
    public static JPanel buildCenteredBar(
            JButton button1, JButton button2, JButton button3) {
        return buildCenteredBar(new JButton[]{
                button1, button2, button3
        });
    }


    /**
     * Builds and returns a centered bar with four buttons.
     *
     * @param button1 the first button to add
     * @param button2 the second button to add
     * @param button3 the third button to add
     * @param button4 the fourth button to add
     * @return a button bar with the given buttons
     */
    public static JPanel buildCenteredBar(
            JButton button1, JButton button2, JButton button3, JButton button4) {
        return buildCenteredBar(new JButton[]{
                button1, button2, button3, button4
        });
    }


    /**
     * Builds and returns a centered bar with five buttons.
     *
     * @param button1 the first button to add
     * @param button2 the second button to add
     * @param button3 the third button to add
     * @param button4 the fourth button to add
     * @param button5 the fifth button to add
     * @return a button bar with the given buttons
     */
    public static JPanel buildCenteredBar(
            JButton button1, JButton button2, JButton button3,
            JButton button4, JButton button5) {
        return buildCenteredBar(new JButton[]{
                button1, button2, button3, button4, button5
        });
    }


    /**
     * Builds and returns a centered button bar with the given buttons.
     *
     * @param buttons an array of buttons to add
     * @return a centered button bar with the given buttons
     */
    public static JPanel buildCenteredBar(JButton[] buttons) {
        ButtonBarBuilder builder = new ButtonBarBuilder();
        builder.addGlue();
        builder.addButton(buttons);
        builder.addGlue();
        return builder.getPanel();
    }


    /**
     * Builds and returns a filled bar with one button.
     *
     * @param button1 the first button to add
     * @return a button bar with the given button
     */
    public static JPanel buildGrowingBar(JButton button1) {
        return buildGrowingBar(new JButton[]{
                button1
        });
    }


    /**
     * Builds and returns a filled button bar with two buttons.
     *
     * @param button1 the first button to add
     * @param button2 the second button to add
     * @return a button bar with the given buttons
     */
    public static JPanel buildGrowingBar(
            JButton button1, JButton button2) {
        return buildGrowingBar(new JButton[]{
                button1, button2
        });
    }


    /**
     * Builds and returns a filled bar with three buttons.
     *
     * @param button1 the first button to add
     * @param button2 the second button to add
     * @param button3 the third button to add
     * @return a button bar with the given buttons
     */
    public static JPanel buildGrowingBar(
            JButton button1, JButton button2, JButton button3) {
        return buildGrowingBar(new JButton[]{
                button1, button2, button3
        });
    }


    /**
     * Builds and returns a filled bar with four buttons.
     *
     * @param button1 the first button to add
     * @param button2 the second button to add
     * @param button3 the third button to add
     * @param button4 the fourth button to add
     * @return a button bar with the given buttons
     */
    public static JPanel buildGrowingBar(
            JButton button1, JButton button2, JButton button3, JButton button4) {
        return buildGrowingBar(new JButton[]{
                button1, button2, button3, button4
        });
    }


    /**
     * Builds and returns a button bar with the given buttons. All button
     * columns will grow with the bar.
     *
     * @param buttons an array of buttons to add
     * @return a filled button bar with the given buttons
     */
    public static JPanel buildGrowingBar(JButton[] buttons) {
        ButtonBarBuilder builder = new ButtonBarBuilder();
        for (JButton button : buttons) {
            builder.addGrowing(button);
        }
        return builder.getPanel();
    }


    // General Purpose Factory Methods: Right Aligned ***********************

    /**
     * Builds and returns a right aligned bar with one button.
     *
     * @param button1 the first button to add
     * @return a button bar with the given button
     */
    public static JPanel buildRightAlignedBar(JButton button1) {
        return buildRightAlignedBar(new JButton[]{
                button1
        });
    }


    /**
     * Builds and returns a right aligned bar with two buttons.
     *
     * @param button1 the first button to add
     * @param button2 the second button to add
     * @return a button bar with the given buttons
     */
    public static JPanel buildRightAlignedBar(
            JButton button1, JButton button2) {
        return buildRightAlignedBar(new JButton[]{
                        button1, button2
                },
                true);
    }


    /**
     * Builds and returns a right aligned bar with three buttons.
     *
     * @param button1 the first button to add
     * @param button2 the second button to add
     * @param button3 the third button to add
     * @return a button bar with the given buttons
     */
    public static JPanel buildRightAlignedBar(
            JButton button1, JButton button2, JButton button3) {
        return buildRightAlignedBar(new JButton[]{
                        button1, button2, button3
                },
                true);
    }


    /**
     * Builds and returns a right aligned bar with four buttons.
     *
     * @param button1 the first button to add
     * @param button2 the second button to add
     * @param button3 the third button to add
     * @param button4 the fourth button to add
     * @return a button bar with the given buttons
     */
    public static JPanel buildRightAlignedBar(
            JButton button1, JButton button2, JButton button3, JButton button4) {
        return buildRightAlignedBar(new JButton[]{
                        button1, button2, button3, button4
                },
                true);
    }


    /**
     * Builds and returns a right aligned bar with five buttons.
     *
     * @param button1 the first button to add
     * @param button2 the second button to add
     * @param button3 the third button to add
     * @param button4 the fourth button to add
     * @param button5 the fifth button to add
     * @return a button bar with the given buttons
     */
    public static JPanel buildRightAlignedBar(
            JButton button1, JButton button2, JButton button3,
            JButton button4, JButton button5) {
        return buildRightAlignedBar(new JButton[]{
                        button1, button2, button3, button4, button5
                },
                true);
    }


    /**
     * Builds and returns a right aligned button bar with the given buttons.
     *
     * @param buttons an array of buttons to add
     * @return a right aligned button bar with the given buttons
     */
    public static JPanel buildRightAlignedBar(JButton[] buttons) {
        ButtonBarBuilder builder = new ButtonBarBuilder();
        builder.addGlue();
        builder.addButton(buttons);
        return builder.getPanel();
    }


    /**
     * Builds and returns a right aligned button bar with the given buttons.
     *
     * @param buttons                an array of buttons to add
     * @param leftToRightButtonOrder the order in which the buttons to add
     * @return a right aligned button bar with the given buttons
     */
    public static JPanel buildRightAlignedBar(
            JButton[] buttons,
            boolean leftToRightButtonOrder) {
        ButtonBarBuilder builder = new ButtonBarBuilder();
        builder.setLeftToRight(leftToRightButtonOrder);
        builder.addGlue();
        builder.addButton(buttons);
        return builder.getPanel();
    }


    // Right Aligned Button Bars with Help in the Left **********************

    /**
     * Builds and returns a right aligned bar with help and one button.
     *
     * @param help    the help button to add on the left side
     * @param button1 the first button to add
     * @return a button bar with the given buttons
     */
    public static JPanel buildHelpBar(JButton help,
                                      JButton button1) {
        return buildHelpBar(help, new JButton[]{
                button1
        });
    }


    /**
     * Builds and returns a right aligned bar with help and two buttons.
     *
     * @param help    the help button to add on the left side
     * @param button1 the first button to add
     * @param button2 the second button to add
     * @return a button bar with the given buttons
     */
    public static JPanel buildHelpBar(JButton help,
                                      JButton button1, JButton button2) {
        return buildHelpBar(help, new JButton[]{
                button1, button2
        });
    }


    /**
     * Builds and returns a right aligned bar with help and three buttons.
     *
     * @param help    the help button to add on the left side
     * @param button1 the first button to add
     * @param button2 the second button to add
     * @param button3 the third button to add
     * @return a button bar with the given buttons
     */
    public static JPanel buildHelpBar(JButton help,
                                      JButton button1, JButton button2, JButton button3) {
        return buildHelpBar(help, new JButton[]{
                button1, button2, button3
        });
    }


    /**
     * Builds and returns a right aligned bar with help and four buttons.
     *
     * @param help    the help button to add on the left side
     * @param button1 the first button to add
     * @param button2 the second button to add
     * @param button3 the third button to add
     * @param button4 the fourth button to add
     * @return a button bar with the given buttons
     */
    public static JPanel buildHelpBar(JButton help,
                                      JButton button1, JButton button2, JButton button3, JButton button4) {
        return buildHelpBar(help, new JButton[]{
                button1, button2, button3, button4
        });
    }


    /**
     * Builds and returns a right aligned bar with help and other buttons.
     *
     * @param help    the help button to add on the left side
     * @param buttons an array of buttons to add
     * @return a right aligned button bar with the given buttons
     */
    public static JPanel buildHelpBar(JButton help, JButton[] buttons) {
        ButtonBarBuilder builder = new ButtonBarBuilder();
        builder.addButton(help);
        builder.addRelatedGap();
        builder.addGlue();
        builder.addButton(buttons);
        return builder.getPanel();
    }
}