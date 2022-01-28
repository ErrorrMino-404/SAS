import businesslogic.CatERing;
import businesslogic.SSException;
import businesslogic.UseCaseLogicException;
import businesslogic.event.ServiceInfo;
import businesslogic.job.Job;
import businesslogic.recipe.Recipe;
import businesslogic.task.SummarySheet;
import businesslogic.task.Task;
import businesslogic.task.kTaskManager;
import com.sun.jdi.ArrayReference;

public class TestDSD2a {
    public static void main (String [] args) throws UseCaseLogicException, SSException {
        CatERing.getInstance().getUserManager().fakeLogin("Lidia");
        System.out.println(CatERing.getInstance().getUserManager().getCurrentUser());
        SummarySheet s = SummarySheet.loadSSId(24);
        kTaskManager taskMgr = CatERing.getInstance().getTaskManager();
        System.out.println("ADD TASK");
        System.out.println("task presenti nella summary sheet 6:");
        taskMgr.loadSS(s);

        taskMgr.deleteTask(s.loadTaskById(125));
        taskMgr.deleteTask(s.loadTaskById(124));
        taskMgr.deleteTask(s.loadTaskById(123));
        SummarySheet.loadSSId(24);
    }
}
