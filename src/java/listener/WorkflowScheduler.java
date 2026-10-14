package listener;

import dao.GiaoDichWorkflowDAO;
import dao.LichHenDAO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebListener;
import java.util.concurrent.*;
import java.util.logging.*;

@WebListener
public class WorkflowScheduler implements ServletContextListener {
    private ScheduledExecutorService executor;
    @Override public void contextInitialized(ServletContextEvent event) {
        executor=Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread=new Thread(r,"autopro-workflows");
            thread.setDaemon(true);
            return thread;
        });
        executor.scheduleWithFixedDelay(() -> {
            try {
                new GiaoDichWorkflowDAO().expirePending();
                new LichHenDAO().remindUpcoming();
            } catch(Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.WARNING,"Workflow maintenance failed; verify database migration",e);
            }
        },15,60,TimeUnit.SECONDS);
    }
    @Override public void contextDestroyed(ServletContextEvent event) {
        if(executor!=null) executor.shutdownNow();
    }
}
