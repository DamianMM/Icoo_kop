/**
 * Created by arvid on 2015-12-27.
 */
package Projekt.Icookop;

import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormattedTextField extends TextField {

    Matcher matcher;

    // Method for checking every key stroke against given regex pattern.
    public FormattedTextField(String regex) {
        Pattern pattern = Pattern.compile(regex);
        matcher = pattern.matcher("");
        EventHandler<KeyEvent> filter = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                String s = getText() + t.getCharacter();
                matcher.reset(s);

                // If key !matches given pattern, or is at end of line, it is consumed.
                if (!(matcher.matches() || matcher.hitEnd())) {
                    t.consume();
                }
            }
        };
        addEventFilter(KeyEvent.KEY_TYPED, filter);
    }
}