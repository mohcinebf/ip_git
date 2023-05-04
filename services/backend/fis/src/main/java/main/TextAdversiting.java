/*
    Informationstypus-Klasse für die Anzeige einer Werbung die nur aus Text besteht. Der Text selber ist im gleichnamigen Attribut zu finden.
 */
package main;

public class TextAdversiting extends Adversiting
{
    String text;
    public TextAdversiting(String text, int freq, double startingBaseValue, double startingActivValue)
    {
        this.type = "Werbung";
        this.adType = "Text";
        this.id = Information.giveNewId();
        this.text = text;
        this.isSingle = false;
        this.frequency = freq;
        this.startingBaseValue = startingBaseValue;
        this.startingActiv = startingActivValue;
    }

    @Override
    public String debugString()
    {
        return super.debugString() + ". Ich habe den folgenden Textinhalt: \"" + text + "'";
    }
}
