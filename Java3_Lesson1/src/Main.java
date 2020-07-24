public class Main
{

    public static void main(String[] args)
    {
        workingArray();
	    fruitPacking();
    }

    public static void workingArray()
    {
        System.out.println("~~~~~~ Задания 1, 2. Работа с массивом. ~~~~~");
        TArrWorker<Integer> vArrWrkInt = new TArrWorker<>(1, 2, 45, 12, 78);
        TArrWorker<String>  vArrWrkStr = new TArrWorker<>("cat", "son", "dog", "sun", "war");

        swapArrayElements(vArrWrkInt, 0, 3); System.out.println();
        swapArrayElements(vArrWrkStr, 2, 4); System.out.println();

        array2ArrayList(vArrWrkInt);
        array2ArrayList(vArrWrkStr);
    }

    public static void swapArrayElements(TArrWorker<?> aArrSwp, int aIdx1, int aIdx2)
    {
        System.out.print("Массив до изменения: ");
        aArrSwp.printArray();

        aArrSwp.swapArrElements(aIdx1, aIdx2);

        System.out.print("Массив после изменения: ");
        aArrSwp.printArray();
    }

    public static void array2ArrayList(TArrWorker<?> aArr)
    {
        aArr.arr2ArrList();

        System.out.print("ArrayList: ");
        aArr.printArrayList();
    }

    public static void fruitPacking()
    {
        System.out.println();
        System.out.println("~~~~~~ Задание 3. Упаковка фруктов. ~~~~~");
        TApple [] vApples  = createApples ();
        TOrange[] vOranges = createOranges();

        TBox<TApple>  vBox4Apples1  = new TBox<>("Коробка для яблок картонная"    );
        TBox<TApple>  vBox4Apples2  = new TBox<>("Коробка для яблок деревянная"   );
        TBox<TOrange> vBox4Oranges1 = new TBox<>("Коробка для апельсинов поменьше");
        TBox<TOrange> vBox4Oranges2 = new TBox<>("Коробка для апельсинов побольше");

        addApples2Box (vApples , vBox4Apples1 , 0, 2);
        addApples2Box (vApples , vBox4Apples2 , 3, 4);
        addOranges2Box(vOranges, vBox4Oranges1, 0, 1);
        addOranges2Box(vOranges, vBox4Oranges2, 2, 6);

        vBox4Apples1 .printBoxInfo(); System.out.println();
        vBox4Apples2 .printBoxInfo(); System.out.println();
        vBox4Oranges1.printBoxInfo(); System.out.println();
        vBox4Oranges2.printBoxInfo(); System.out.println();

        vBox4Apples1 .compare(vBox4Apples2 ); System.out.println();
        vBox4Apples1 .compare(vBox4Oranges1); System.out.println();
        vBox4Apples1 .compare(vBox4Oranges2); System.out.println();
        vBox4Apples2 .compare(vBox4Apples1 ); System.out.println();
        vBox4Apples2 .compare(vBox4Oranges1); System.out.println();
        vBox4Apples2 .compare(vBox4Oranges2); System.out.println();
        vBox4Oranges1.compare(vBox4Apples1 ); System.out.println();
        vBox4Oranges1.compare(vBox4Apples2 ); System.out.println();
        vBox4Oranges1.compare(vBox4Oranges2); System.out.println();
        vBox4Oranges2.compare(vBox4Apples1 ); System.out.println();
        vBox4Oranges2.compare(vBox4Apples2 ); System.out.println();
        vBox4Oranges2.compare(vBox4Oranges1); System.out.println();

        vBox4Apples1 .send2AnotherBox(vBox4Apples2 );
        vBox4Oranges1.send2AnotherBox(vBox4Oranges2);

        vBox4Apples1 .printBoxInfo(); System.out.println();
        vBox4Apples2 .printBoxInfo(); System.out.println();
        vBox4Oranges1.printBoxInfo(); System.out.println();
        vBox4Oranges2.printBoxInfo();

    }

    public static TApple[] createApples()
    {
        return new TApple[]
                {
                        new TApple("Яблоко Венгерка спелое")
                      , new TApple("Яблоко свежий урожай непонятного сорта")
                      , new TApple("Яблоко Фуши (мое любимое)")
                      , new TApple("Яблоко Грушовка с пятнами")
                      , new TApple("Яблоко Гренни подгнившее")
                };
    }

    public static TOrange[] createOranges()
    {
        return new TOrange[]
                {
                        new TOrange("Апельсин мороканский")
                      , new TOrange("Апельсин сладкий")
                      , new TOrange("Апельсин с косточками")
                      , new TOrange("Апельсин красный Вашингтон")
                      , new TOrange("Апельсин кислый")
                      , new TOrange("Апельсин испорченный")
                      , new TOrange("Апельсин египетский")
                };
    }

    public static void addApples2Box(TApple[] aAppleArr, TBox<TApple> aBox, int aIdxFrom, int aIdxTo)
    {
        for (int i = aIdxFrom; i <= aIdxTo; i++)
        {
            aBox.add(aAppleArr[i]);
        }
    }

    public static void addOranges2Box(TOrange[] aOrangeArr, TBox<TOrange> aBox, int aIdxFrom, int aIdxTo)
    {
        for (int i = aIdxFrom; i <= aIdxTo; i++)
        {
            aBox.add(aOrangeArr[i]);
        }
    }

}
