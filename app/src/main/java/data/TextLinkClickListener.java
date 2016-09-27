package data;

/**
 * Created by Obakeng Moshane on 2016-04-04.
 */
import android.view.View;

public interface TextLinkClickListener
{


    //  This method is called when the TextLink is clicked from LinkEnabledTextView

    public void onTextLinkClick(View textView, String clickedString);
}