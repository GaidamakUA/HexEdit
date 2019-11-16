package at.HexLib.GUI.gui;

//@author Santhosh Kumar T - santhosh@in.fiorano.com
//http://www.jroller.com/santhosh/entry/file_path_autocompletion
//Modifications and additions by Martin Wildam 2010-07-21
//See also http://pastebin.com/e6CnxVKY
/*Modifications by LeO 2011-02-23 (
 * - set selection to the selected item with Key-Interaction
 * - firePropertyChanged ONLY when no popup is shown AND user pressed ENTER
 */

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;


public abstract class AutoCompleter {

    protected JList list = new JList();
    protected JPopupMenu popup = new JPopupMenu();
    protected JTextComponent textComp;
    private static final String AUTOCOMPLETER = "AUTOCOMPLETER"; // NOI18N
    public static final String ENTER_RELEASED = "Enter_released";

    public AutoCompleter(JTextComponent comp) {
        textComp = comp;
        textComp.putClientProperty(AUTOCOMPLETER, this);
        textComp.registerKeyboardAction(acceptAction,
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                JComponent.WHEN_FOCUSED);

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);

        list.putClientProperty(AUTOCOMPLETER, this);
        list.setFocusable(false);
        scroll.getVerticalScrollBar().setFocusable(false);
        scroll.getHorizontalScrollBar().setFocusable(false);

        list.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent me) {
                Point p = new Point(me.getX(), me.getY());
                list.setSelectedIndex(list.locationToIndex(p));
                JComponent tf = (JComponent) me.getSource();
                AutoCompleter completer =
                        (AutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
                completer.acceptedListItem((String) completer.list.getSelectedValue());
                completer.popup.setVisible(false);
            }

        });

        popup.setBorder(BorderFactory.createLineBorder(Color.black));
        popup.add(scroll);
        popup.setFocusable(false);

        if (textComp instanceof JTextField) {
            textComp.registerKeyboardAction(showAction,
                    KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,
                            0),
                    JComponent.WHEN_FOCUSED);
            // textComp.getDocument().addDocumentListener(documentListener);
        } else {
            textComp.registerKeyboardAction(showAction,
                    KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,
                            KeyEvent.CTRL_MASK),
                    JComponent.WHEN_FOCUSED);
        }
        textComp.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                if (e == null) {
                    return;
                }
                Component cmp = e.getOppositeComponent();
                if (cmp == null) {
                    return;
                }
                if (!(cmp == null)
                        && !"javax.swing.JRootPane".equals(cmp.getClass().getName())) {
                    JComponent tf = (JComponent) e.getSource();
                    AutoCompleter completer =
                            (AutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
                    SwingUtilities.invokeLater(new AutoCompletePopupCloser(completer.popup,
                            cmp));
                }
            }

            @Override
            public void focusGained(FocusEvent e) {
                if (e == null) {
                    return;
                }
                JComponent tf = (JComponent) e.getSource();
                AutoCompleter completer =
                        (AutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
                completer.showPopup();
            }

        });

        textComp.addKeyListener(new KeyAdapter() {

            public void keyReleased(KeyEvent e) {
                if (e == null) {
                    return;
                }

                JComponent tf = (JComponent) e.getSource();
                AutoCompleter completer =
                        (AutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
                int keyCode = e.getKeyCode();
                if (keyCode != KeyEvent.VK_ESCAPE && keyCode != KeyEvent.VK_TAB
                        && keyCode != KeyEvent.VK_UP && keyCode != KeyEvent.VK_DOWN
                        && keyCode != KeyEvent.VK_HOME && keyCode != KeyEvent.VK_END
                        && keyCode != KeyEvent.VK_PAGE_DOWN
                        && keyCode != KeyEvent.VK_PAGE_UP && keyCode != KeyEvent.VK_ENTER) {

                    completer.showPopup();
                } else if (keyCode == KeyEvent.VK_ENTER) {
                } else {
                    int lastIndex = completer.list.getModel().getSize() - 1;
                    int pageSize = completer.list.getVisibleRowCount();
                    int pageStartIndex = Math.max(0, completer.list.getSelectedIndex());

                    if (keyCode == KeyEvent.VK_PAGE_DOWN) {
                        pageStartIndex = completer.list.getLastVisibleIndex() + pageSize;
                        if (pageStartIndex > lastIndex) {
                            pageStartIndex = lastIndex;
                        }
                    } else if (keyCode == KeyEvent.VK_PAGE_UP) {
                        pageStartIndex = completer.list.getFirstVisibleIndex() - pageSize;
                        if (pageStartIndex < 0) {
                            pageStartIndex = 0;
                        }

                    } else if (keyCode == KeyEvent.VK_HOME) {
                        pageStartIndex = 0;
                    } else if (keyCode == KeyEvent.VK_END) {
                        pageStartIndex = lastIndex;
                    }
                    completer.list.setSelectedIndex(pageStartIndex);
                    completer.list.ensureIndexIsVisible(pageStartIndex);
                }
            }

        });

        textComp.registerKeyboardAction(upAction,
                KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
                JComponent.WHEN_FOCUSED);
        textComp.registerKeyboardAction(hidePopupAction,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,
                        0),
                JComponent.WHEN_FOCUSED);

        popup.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                // textComp.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
                // 0));
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }

        });

        list.setRequestFocusEnabled(false);
    }

    static Action acceptAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            AutoCompleter completer =
                    (AutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
            if (!completer.popup.isShowing()) {
                tf.firePropertyChange(ENTER_RELEASED,
                        false,
                        true);
            } else {
                completer.acceptedListItem((String) completer.list.getSelectedValue());
                completer.popup.setVisible(false);
            }
        }

    };

    protected void showPopup() {
        popup.setVisible(false);
        if (textComp.isEnabled() && updateListData()
                && list.getModel().getSize() != 0) {
            int size = list.getModel().getSize();
            list.setVisibleRowCount(size < 10 ? size : 10);

            int x = 0;
            try {
                int pos =
                        Math.min(textComp.getCaret().getDot(), textComp.getCaret()
                                .getMark());
                x = textComp.getUI().modelToView(textComp, pos).x;
            } catch (BadLocationException e) {
                // this should never happen!!!
                e.printStackTrace();
            }

            popup.show(textComp, x, textComp.getHeight());
        } else {
            popup.setVisible(false);
        }
        textComp.requestFocus();
    }


    static Action showAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            AutoCompleter completer =
                    (AutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
            if (tf.isEnabled()) {
                if (completer.popup.isVisible()) {
                    completer.selectNextPossibleValue();
                } else {
                    completer.showPopup();
                }
            }
        }


    };
    static Action upAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            AutoCompleter completer =
                    (AutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
            if (tf.isEnabled()) {
                if (completer.popup.isVisible()) {
                    completer.selectPreviousPossibleValue();
                }
            }
        }


    };
    static Action hidePopupAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            AutoCompleter completer =
                    (AutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
            if (tf.isEnabled()) {
                completer.popup.setVisible(false);
            }
        }


    };

    /**
     * Selects the next item in the list. It won't change the selection if the currently
     * selected item is already the last item.
     */
    protected void selectNextPossibleValue() {
        int si = list.getSelectedIndex();

        if (si < list.getModel().getSize() - 1) {
            list.setSelectedIndex(si + 1);
            list.ensureIndexIsVisible(si + 1);
        }
    }


    /**
     * Selects the previous item in the list. It won't change the selection if the currently
     * selected item is already the first item.
     */
    protected void selectPreviousPossibleValue() {
        int si = list.getSelectedIndex();

        if (si > 0) {
            list.setSelectedIndex(si - 1);
            list.ensureIndexIsVisible(si - 1);
        }
    }


    private class AutoCompletePopupCloser implements Runnable {

        private JPopupMenu popupMenu = null;
        private Component nextFocusTo = null;

        public AutoCompletePopupCloser(JPopupMenu popupMenu, Component cmp) {
            this.popupMenu = popupMenu;
            this.nextFocusTo = cmp;
        }


        @Override
        public void run() {
            popupMenu.setVisible(false);
            if (nextFocusTo != null) {
                nextFocusTo.requestFocus();
            }
        }


    }


    protected abstract boolean updateListData(); // update list model depending on the data
    // in textfield


    protected abstract void acceptedListItem(String selected); // user has selected some
    // item in the list. update
    // textfield accordingly...


}