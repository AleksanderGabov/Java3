import java.util.ArrayList;

public class TBox<T extends TFruit>
{
    private final String       fName;
    private final ArrayList<T> fFruits;

    public TBox(String aName)
    {
        fName   = aName;
        fFruits = new ArrayList<>();
    }

    public float getWeight()
    {
        return fFruits.size() > 0 ? fFruits.size() * fFruits.get(0).getWeight() : 0;
    }

    public void compare(TBox<?> aAnotherBox)
    {
        System.out.println(String.format("Результат сравнения коробки \"%s\" с коробкой \"%s\":"
                , fName
                , aAnotherBox.fName));
        if (getWeight() == aAnotherBox.getWeight())
             System.out.println("Вес одинаковый");
        else System.out.println("Вес разный");
    }

    public void send2AnotherBox(TBox<T> aAnotherBox)
    {
        aAnotherBox.fFruits.addAll(fFruits);
        fFruits.clear();
    }

    public void add(T aFruit)
    {
        fFruits.add(aFruit);
    }

    public void printBoxInfo()
    {
        System.out.println(String.format("Коробка \"%s\":", fName));
        System.out.println("Содержимое: " + fFruits);
        System.out.println("Вес       : " + getWeight());
    }
}
