package Task1;

public class PrintingThreadsClass
{
    private final Object fMonitor  = new Object();
    private       char   fPrintNow = 'A';
    public static void main(String[] args)
    {
        threadWorking();
    }

    public static void threadWorking()
    {
        PrintingThreadsClass vPrintThr = new PrintingThreadsClass();
        new Thread(() -> vPrintThr.printLetter('A', 'B')).start();
        new Thread(() -> vPrintThr.printLetter('B', 'C')).start();
        new Thread(() -> vPrintThr.printLetter('C', 'A')).start();
    }

    public void printLetter(char aLetter, char aLetterNext)
    {
        synchronized (fMonitor)
        {
            try
            {
                for (int i = 1; i <= 5; i++)
                {
                    while (fPrintNow != aLetter)
                    {
                        fMonitor.wait();
                    }
                    System.out.println(aLetter);
                    fPrintNow = aLetterNext;
                    fMonitor.notifyAll();
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
