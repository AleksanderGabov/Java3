import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TRace
{
    private final ArrayList<TStage> stages;
    public  ArrayList<TStage> getStages() { return stages; }

    private final CyclicBarrier  fWaitingReady ;
    private final CountDownLatch fWaitingStart ;
    private final CountDownLatch fWaitingFinish;

    private       Boolean fIsWinnerKnown = false;
    private final Lock    fSetWinnerLock = new ReentrantLock();

    public CyclicBarrier  getWaitingReady()  { return fWaitingReady ; }
    public CountDownLatch getWaitingStart()  { return fWaitingStart ; }
    public CountDownLatch getWaitingFinish() { return fWaitingFinish; }
    public Lock           getSetWinnerLock() { return fSetWinnerLock; }

    public Boolean getIsWinnerKnown() { return fIsWinnerKnown; }
    public void setIsWinnerKnown(Boolean aIsWinnerKnown) { fIsWinnerKnown = aIsWinnerKnown; }

    public  TRace(int aCarsCount, TStage... stages)
    {
        this.stages = new ArrayList<>(Arrays.asList(stages));

        fWaitingReady  = new CyclicBarrier (aCarsCount);
        fWaitingStart  = new CountDownLatch(aCarsCount);
        fWaitingFinish = new CountDownLatch(aCarsCount);
    }
}
