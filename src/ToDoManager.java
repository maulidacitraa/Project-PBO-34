import java.util.ArrayList;

public class ToDoManager {

    private ArrayList<ToDo> todos = new ArrayList<>();

    public void add(ToDo todo) {
        todos.add(todo);
        System.out.println("Berhasil menambahkan!");
    }

    public void remove(int index) {
        if (index < 0 || index >= todos.size()) {
            System.out.println("Index invalid!");
            return;
        }
        todos.remove(index);
        System.out.println("Berhasil dihapus!");
    }

    public void showAll() {
        if (todos.isEmpty()) {
            System.out.println("Belum ada jadwal.");
            return;
        }
        for (int i = 0; i < todos.size(); i++) {
            System.out.println(i + ". ");
            todos.get(i).showDetails();
        }
    }
}
