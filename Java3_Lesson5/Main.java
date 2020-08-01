public class Main
{
    public static final int CARS_COUNT = 4;

    public static void main(String[] args)
    {
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");
        TRace  race = new TRace(CARS_COUNT, new TRoad(60), new TTunnel(CARS_COUNT / 2), new TRoad(40));
        TCar[] cars = new TCar[CARS_COUNT];

        for (int i = 0; i < cars.length; i++)
        {
            cars[i] = new TCar(race, 20 + (int) (Math.random() * 10));
        }

        for (int i = 0; i < cars.length; i++)
        {
            new Thread(cars[i]).start();
        }

        try
        {
            race.getWaitingStart().await();
            System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");

            race.getWaitingFinish().await();
            System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");
        }
        catch (InterruptedException e)
        {
            System.out.println("Ошибка при ожидании старта/финиша машин.");
            e.printStackTrace();
        }
    }
}
