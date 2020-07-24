public abstract class TFruit
{
    private final float  fWeight;
    private final String fDescription;

    public TFruit(String aDescription, float aWeight)
    {
        fDescription = aDescription;
        fWeight      = aWeight;
    }

    @Override
    public String toString()
    {
        return fDescription;
    }

    public float  getWeight() { return fWeight; }
}
