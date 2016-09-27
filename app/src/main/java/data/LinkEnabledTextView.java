package data;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import data.Hyperlink;

/***
 *
 * Code by  Kamesh Rao
 * https://www.javacodegeeks.com/2012/09/android-custom-hyperlinked-textview.html
 */
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class LinkEnabledTextView extends TextView {
    // The String Containing the Text that we have to gather links from private SpannableString linkableText;
// Populating and gathering all the links that are present in the Text
    private ArrayList listOfLinks;
    private SpannableString linkableText;

    // The type of tag that was clicked
    public enum Type {
        USER, HASTAG, URL
    }

    // A Listener Class for generally sending the Clicks to the one which requires it
    TextLinkClickListener mListener;

    // Pattern for gathering @usernames from the Text
    Pattern userNamePattern = Pattern.compile("(@[a-zA-Z0-9_]+)");

    // Pattern for gathering #hasttags from the Text
    Pattern hashTagsPattern = Pattern.compile("(#[a-zA-Z0-9_-]+)");

    // Pattern for gathering http:// links from the Text
    Pattern hyperLinksPattern = Pattern.compile("([Hh][tT][tT][pP][sS]?:\\/\\/[^ ,'\'>\\]\\)]*[^\\. ,'\'>\\]\\)])");

    public LinkEnabledTextView(Context context) {
        super(context);
        init();
    }

    public LinkEnabledTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        listOfLinks = new ArrayList();
    }

    public void gatherLinksForText(String text) {
        linkableText = new SpannableString(text);
// gatherLinks basically collects the Links depending upon the Pattern that we supply
// and add the links to the ArrayList of the links

        gatherLinks(listOfLinks, linkableText, userNamePattern, Type.USER);
        gatherLinks(listOfLinks, linkableText, hashTagsPattern, Type.HASTAG);
        gatherLinks(listOfLinks, linkableText, hyperLinksPattern, Type.URL);

        for (int i = 0; i < listOfLinks.size(); i++) {
            Hyperlink linkSpec = (Hyperlink) listOfLinks.get(i);
//android.util.Log.v("listOfLinks :: " + linkSpec.textSpan, "listOfLinks :: " + linkSpec.textSpan);

// this process here makes the Clickable Links from the text

            linkableText.setSpan(linkSpec.span, linkSpec.start, linkSpec.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

// sets the text for the TextView with enabled links

        setText(linkableText);
    }

// sets the Listener for later click propagation purpose

    public void setOnTextLinkClickListener(TextLinkClickListener newListener) {
        mListener = newListener;
    }

// The Method mainly performs the Regex Comparison for the Pattern and adds them to
// listOfLinks array list

    private final void gatherLinks(ArrayList links, Spannable s, Pattern pattern, Type type) {
// Matcher matching the pattern
        Matcher m = pattern.matcher(s);

        while (m.find()) {
            int start = m.start();
            int end = m.end();

// Hyperlink is basically used like a structure for storing the information about
// where the link was found.

            Hyperlink spec = new Hyperlink();

            spec.textSpan = s.subSequence(start, end);
            spec.span = new InternalURLSpan(spec.textSpan.toString(), type);
            spec.start = start;
            spec.end = end;

            links.add(spec);
        }
    }

// This is class which gives us the clicks on the links which we then can use.

    public class InternalURLSpan extends ClickableSpan {
        private String clickedSpan;
        private Type type;

        public InternalURLSpan(String clickedString, Type type) {
            clickedSpan = clickedString;
            this.type = type;
        }

        @Override
        public void onClick(View textView) {
            mListener.onTextLinkClick(textView, clickedSpan, type);
        }
    }



    public interface TextLinkClickListener {

// This method is called when the TextLink is clicked from LinkEnabledTextView

        public void onTextLinkClick(View textView, String clickedString, Type type);
    }

}