import java.util.ArrayList;
import java.util.Arrays;

public class TArrWorker<T>
{
    private final T[] fArr;
    private ArrayList<T> fArrList;

    @SafeVarargs
    public TArrWorker(T... aArr)
    {
        fArr = aArr;
    }

    public void swapArrElements(int aIdx1, int aIdx2)
    {
        T vBuffer;
        vBuffer = fArr[aIdx2];
        fArr[aIdx2] = fArr[aIdx1];
        fArr[aIdx1] = vBuffer;
    }

    public void arr2ArrList()
    {
        fArrList = new ArrayList<>();
        fArrList.addAll(Arrays.asList(fArr));
    }

    public void printArray()
    {
        System.out.println(Arrays.toString(fArr));
    }

    public void printArrayList()
    {
        System.out.println(fArrList);
    }
}
