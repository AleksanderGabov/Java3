public class TCar implements Runnable
{
    private static int CARS_COUNT;
    private final TRace  race ;
    private final int    speed;
    private final String name ;

    public String getName () { return name ; }
    public int    getSpeed() { return speed; }

    public TCar(TRace race, int speed)
    {
        this.race  = race;
        this.speed = speed;
        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;
    }

    private void checkWin()
    {
        try
        {
            race.getSetWinnerLock().lock();
            if (!race.getIsWinnerKnown())
            {
                System.out.println(this.name + " - WIN");
                race.setIsWinnerKnown(true);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            race.getSetWinnerLock().unlock();
        }
    }

    @Override
    public void run()
    {
        try
        {
            System.out.println(this.name + " готовится");
            Thread.sleep(500 + (int)(Math.random() * 800));
            System.out.println(this.name + " готов");

            race.getWaitingReady().await();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        race.getWaitingStart().countDown();
        for (int i = 0; i < race.getStages().size(); i++)
        {
            race.getStages().get(i).go(this);
        }
        checkWin();
        race.getWaitingFinish().countDown();
    }
}
