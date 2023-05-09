/*
    Informationstypus-Klasse für die Anzeige einer Werbung die nur aus Text besteht. Der Text selber ist im gleichnamigen Attribut zu finden.
 */
package DataClasses;

import DataClasses.Adversiting;
import DataClasses.Information;

public class TextAdversiting extends Adversiting
{
    String text;
    public TextAdversiting(String text, int freq, double startingBaseValue, double startingActivValue, int duration)
    {
        super();
        this.type = "Werbung";
        this.adType = "Text";
        this.text = text; // should be replaced by msg attribute from superclass
        this.msg = text;
        this.isSingle = false;
        this.frequency = freq;
        this.startingBaseValue = startingBaseValue;
        this.startingActiv = startingActivValue;
        this.duration = duration;
    }

    @Override
    public String debugString()
    {
        return super.debugString() + ". Ich habe den folgenden Textinhalt: \"" + text + "'";
    }
}
