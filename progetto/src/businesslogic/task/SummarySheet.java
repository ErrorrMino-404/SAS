package businesslogic.task;
import businesslogic.SSException;
import businesslogic.event.ServiceInfo;
import businesslogic.job.Job;
import businesslogic.recipe.Recipe;
import businesslogic.shift.TurnKitchen;
import businesslogic.user.User;
import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.*;
import java.util.ArrayList;
/*a mio parere nel caso di utilizzo di summarysheet stiamo utilizzando un pattern singleton, perché ci può essere
* una singola istanza di questo oggetto*/
public class SummarySheet {
    private ArrayList<Task> taskList;
    private User owner;
    private ServiceInfo serviceUsed;
    private int id;


    public SummarySheet(ServiceInfo s, User user, Menu menu) {
        this.owner = user;
        this.serviceUsed = s;
        ArrayList<Recipe> arrayListRecipe = menu.getAllRecipe();
        taskList = new ArrayList<Task>();
        for(int i=0;i<arrayListRecipe.size();i++){
            Task task = new Task(arrayListRecipe.get(i));
            taskList.add(task);
        }
    }
    public SummarySheet(int id){
        this.id=id;
    }
    public boolean isOwner(User u) {
        return u.equals(owner);
    }

    public Task addTask(Job job) {
        Task turn = new Task(job);
        this.taskList.add(turn);
        return turn;
    }
    public void deleteTask(Task task) {
        this.taskList.remove(task);

    }
    public ArrayList<Task> getTaskList(){return this.taskList;}

    public ArrayList<Task> sortTask(ArrayList<Task> newtl) {
        this.taskList= newtl;
        return taskList;
    }

    public boolean contains(Task task){
        return taskList.contains(task);
    }

    public void assigneTask(Task task, ArrayList<TurnKitchen> tlList, int portion, Time duration, User cook) {
        task.assigneTask(task,tlList,portion,duration,cook);
    }

    public void modifyTask(Task task, ArrayList<TurnKitchen> tlList, int portion, Time duration, User cook)     throws SSException {
        task.modifyTask(task,tlList,portion,duration,cook);

    }
    public void disassignTask(Task task) {
        task.disassignTask();
    }

    public void taskDone(Task task) {
        task.done();
    }
    public String getServiceName(){return this.serviceUsed.getName();}

    public int getId(){return this.id;}

    public void stampTask(){
        for (Task t:taskList) {
            System.out.println(Recipe.loadRecipeById(t.getIdRecipe()));
        }
    }
    public String toString(){

        return "User= "+ this.owner.getUserName() + "\n"+
                "Service info= "+this.serviceUsed.getName()+"\n"+
                "ID summary sheet= "+this.id+"\n";
    }


    /*PERSISTANCE*/
    public static void saveNewSS(SummarySheet ss) {
            String ssInsert = "INSERT INTO catering.SummarySheet(user, service_id,service_name) VALUES (?, ?,?);";
            int[] result = PersistenceManager.executeBatchUpdate(ssInsert, 1, new BatchUpdateHandler() {
                @Override
                public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                    ps.setInt(1,(ss.owner.getId()));
                    ps.setInt(2, ss.serviceUsed.getId());
                    ps.setString(3, ss.serviceUsed.getName());
                }

                @Override
                public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                    if (count == 0) {
                        ss.id = rs.getInt(1);
                    }
                }
            });
            Menu m = ss.serviceUsed.getMenu();
            ss.taskList =new ArrayList<Task>();
            ArrayList<Recipe> arrayListRecipe = m.getAllRecipe();
            for(int i=0;i<arrayListRecipe.size();i++) {
                Task t = new Task(arrayListRecipe.get(i));
                ss.taskList.add(t);
                t.saveNewTaskInSS(t);
            }

    }
    public static SummarySheet loadSSId(int id){
        SummarySheet ss = new SummarySheet(id);
        String query = "SELECT id,user,service_id FROM summarysheet WHERE id = " + id;
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                ss.owner = User.loadUserById(rs.getInt("user"));
                ss.serviceUsed = ServiceInfo.loadServiceById(rs.getInt("service_id"));

            }
        });
        ss.taskList =new ArrayList<Task>();
        query = "SELECT id FROM task WHERE id_summarysheet = " + id;
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                        ss.taskList.add(Task.loadTaskById(rs.getInt("id")));
            }
        });
        return ss;
    }


}


