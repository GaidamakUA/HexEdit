//package at.HexLib.GUI.gui.panels;
//
//import java.awt.Font;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
//import javax.swing.Icon;
//import javax.swing.JButton;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.JSlider;
//import javax.swing.SwingUtilities;
//import javax.swing.UIDefaults;
//import javax.swing.border.EmptyBorder;
//import javax.swing.event.ChangeEvent;
//import javax.swing.event.ChangeListener;
//import javax.swing.plaf.FontUIResource;
//
//import org.pushingpixels.substance.api.SubstanceLookAndFeel;
//import org.pushingpixels.substance.api.fonts.FontPolicy;
//import org.pushingpixels.substance.api.fonts.FontSet;
//
//import at.HexLib.GUI.gui.ImageProvider;
//import at.HexLib.GUI.main.HexEditSample;
//
//import com.jgoodies.forms.builder.DefaultFormBuilder;
//import com.jgoodies.forms.layout.FormLayout;
//
//public class FontSizePanel {
//
//  private static class WrapperFontSet implements FontSet {
//
//    private final int     extra;
//
//    private final FontSet delegate;
//
//    public WrapperFontSet(FontSet delegate, int extra) {
//      super();
//      this.delegate = delegate;
//      this.extra = extra;
//    }
//
//    private FontUIResource getWrappedFont(FontUIResource systemFont) {
//      return new FontUIResource(systemFont.getFontName(),
//                                systemFont.getStyle(),
//                                extra
//      // systemFont.getSize() + extra
//      );
//    }
//
//    public FontUIResource getControlFont() {
//      return getWrappedFont(delegate.getControlFont());
//    }
//
//    public FontUIResource getMenuFont() {
//      return getWrappedFont(delegate.getMenuFont());
//    }
//
//    public FontUIResource getMessageFont() {
//      return getWrappedFont(delegate.getMessageFont());
//    }
//
//    public FontUIResource getSmallFont() {
//      return getWrappedFont(delegate.getSmallFont());
//    }
//
//    public FontUIResource getTitleFont() {
//      return getWrappedFont(delegate.getTitleFont());
//    }
//
//    public FontUIResource getWindowTitleFont() {
//      // FontUIResource f = this.getWrappedFont(this.delegate
//      // .getWindowTitleFont());
//      // return new FontUIResource(f.deriveFont(Font.BOLD, f.getSize() +
//      // 1));
//      return getWrappedFont(delegate.getWindowTitleFont());
//    }
//  }
//
//  private static class Tahoma11FontSet implements FontSet {
//
//    static String                defaultFont =
//                                                 SubstanceLookAndFeel.getFontPolicy()
//                                                                     .getFontSet("Substance",
//                                                                                 null)
//                                                                     .getControlFont()
//                                                                     .getFontName();
//    private final FontUIResource tahoma11    = new FontUIResource(defaultFont,
//                                                                    Font.PLAIN,
//                                                                    muFontHelp.getSize());
//
//    public FontUIResource getControlFont() {
//      return tahoma11;
//    }
//
//    public FontUIResource getMenuFont() {
//      return tahoma11;
//    }
//
//    public FontUIResource getMessageFont() {
//      return tahoma11;
//    }
//
//    public FontUIResource getSmallFont() {
//      return tahoma11;
//    }
//
//    public FontUIResource getTitleFont() {
//      return tahoma11;
//    }
//
//    public FontUIResource getWindowTitleFont() {
//      return tahoma11;
//    }
//  }
//
//  public static JSlider fontSizeSlider;
//  private static int    sizeFont;
//  private static Font   muFontHelp = Font.decode("default");
//
//  public static JPanel getPanel(final HexEditSample hexEditor) {
//    FormLayout lm =
//        new FormLayout("fill:pref, 2dlu, fill:pref, 0dlu, fill:min:grow, 0dlu, fill:pref, 2dlu, fill:pref,3dlu",
//                       "c:d:g");
//    DefaultFormBuilder builder = new DefaultFormBuilder(lm);
//    builder.setBorder(new EmptyBorder(0, 0, 0, 0));
//
//    final JLabel fontSizeLabel = new JLabel();
//    sizeFont =
//        SubstanceLookAndFeel.getFontPolicy()
//                            .getFontSet("Substance", null)
//                            .getControlFont()
//                            .getSize();
//    fontSizeLabel.setText(sizeFont + " pt.");
//    builder.append(fontSizeLabel);
//    // fontSizeLabel.add(Box.createHorizontalStrut(5));
//
//    Icon zoomOutIcon = ImageProvider.getInstance().getZoomOutIcon();
//    // new ImageIcon(Thread.currentThread()
//    // .getContextClassLoader()
//    // .getResource("test/check/icons/16/zoom_out.png"));
//    JLabel zoomOutLabel = new JLabel(zoomOutIcon);
//    builder.append(zoomOutLabel);
//
//    fontSizeSlider = new JSlider(9, 19, sizeFont);
//    fontSizeSlider.setFocusable(false);
//    fontSizeSlider.setMajorTickSpacing(2);
//    fontSizeSlider.setMinorTickSpacing(1);
//    fontSizeSlider.setPaintLabels(true);
//    fontSizeSlider.setPaintTicks(true);
//
//    // fontSizeSlider.setOpaque(false);
//    fontSizeSlider.setToolTipText("Controls the global font set size.");
//    // "Resets Substance as the current LAF.");
//    fontSizeSlider.addChangeListener(new ChangeListener() {
//
//      public void stateChanged(ChangeEvent e) {
//        // if the value is adjusting - ignore. This is done
//        // to make CPU usage better.
//        if (!fontSizeSlider.getModel().getValueIsAdjusting()) {
//          final int newValue = fontSizeSlider.getValue();
//          SwingUtilities.invokeLater(new Runnable() {
//
//            public void run() {
//              setFontSize(newValue);
//
//              final int fontSize =
//                  SubstanceLookAndFeel.getFontPolicy()
//                                      .getFontSet("Substance", null)
//                                      .getControlFont()
//                                      .getSize();
//              fontSizeLabel.setText(fontSize + " pt.");
//              if (hexEditor != null) {
//                // userOptions.pack();
//                hexEditor.setMasterFrameSize(fontSize, (double) fontSize
//                                                         / (double) sizeFont);
//              }
//              sizeFont = fontSize;
//            }
//          });
//        }
//      }
//    });
//    builder.append(fontSizeSlider);
//
//    Icon zoomInIcon = ImageProvider.getInstance().getZoomInIcon();
//    // new ImageIcon(Thread.currentThread()
//    // .getContextClassLoader()
//    // .getResource("test/check/icons/16/zoom_in.png"));
//    JLabel zoomInLabel = new JLabel(zoomInIcon);
//    builder.append(zoomInLabel);
//
//    JButton tahoma =
//        new JButton(Tahoma11FontSet.defaultFont + " " + muFontHelp.getSize());
//    tahoma.putClientProperty(SubstanceLookAndFeel.BUTTON_NO_MIN_SIZE_PROPERTY,
//                             Boolean.TRUE);
//    tahoma.addActionListener(new ActionListener() {
//
//      @Override
//      public void actionPerformed(ActionEvent e) {
//        SwingUtilities.invokeLater(new Runnable() {
//
//          public void run() {
//            SubstanceLookAndFeel.setFontPolicy(null);
//            FontPolicy newFontPolicy = new FontPolicy() {
//
//              public FontSet getFontSet(String lafName, UIDefaults table) {
//                return new Tahoma11FontSet();
//              }
//            };
//
//            SubstanceLookAndFeel.setFontPolicy(newFontPolicy);
//
//            final int sizeFont =
//                SubstanceLookAndFeel.getFontPolicy()
//                                    .getFontSet("Substance", null)
//                                    .getControlFont()
//                                    .getSize();
//            fontSizeLabel.setText(sizeFont + " pt.");
//            fontSizeSlider.setValue(sizeFont);
//          }
//        });
//      }
//    });
//    builder.append(tahoma);
//
//    JPanel result = builder.getPanel();
//    // result.setOpaque(false);
//    return result;
//  }
//
//  public static void setFontSize(final int newValue) {
//    SubstanceLookAndFeel.setFontPolicy(null);
//    final FontSet substanceCoreFontSet =
//        SubstanceLookAndFeel.getFontPolicy().getFontSet("Substance", null);
//    FontPolicy newFontPolicy = new FontPolicy() {
//
//      public FontSet getFontSet(String lafName, UIDefaults table) {
//        return new WrapperFontSet(substanceCoreFontSet, newValue);
//      }
//    };
//
//    SubstanceLookAndFeel.setFontPolicy(newFontPolicy);
//  }
//}
