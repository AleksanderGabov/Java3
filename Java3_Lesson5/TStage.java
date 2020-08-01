public abstract class TStage
{
    protected int    length;
    protected String description;

    public String getDescription() { return description; }

    public abstract void go(TCar c);
}
