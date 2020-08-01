import java.util.concurrent.Semaphore;

public class TTunnel extends TStage
{
    private final Semaphore fOccupation;
    private int             fCounter = 0;

    public TTunnel(int aThroughput)
    {
        this.length      = 80;
        this.description = "Тоннель " + length + " метров";

        fOccupation = new Semaphore(aThroughput);
    }

    private void printInfo()
    {
        System.out.println("Количество машин в тоннеле: " + fCounter);
    }

    @Override
    public void go(TCar c)
    {
        try
        {
            try
            {
                System.out.println(c.getName() + " готовится к этапу(ждет): " + description);
                fOccupation.acquire();
                System.out.println(c.getName() + " начал этап: " + description);
                fCounter++;
                printInfo();
                Thread.sleep(length / c.getSpeed() * 1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            finally
            {
                System.out.println(c.getName() + " закончил этап: " + description);
                fCounter--;
                printInfo();
                fOccupation.release();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
