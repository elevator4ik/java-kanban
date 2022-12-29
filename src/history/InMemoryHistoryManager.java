package history;

import modul.Node;
import modul.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    CustomLinkedList inMemoryHistoryManager = new CustomLinkedList();

    @Override
    public void add(Task task) {

        inMemoryHistoryManager.linkLast(task);

    }

    @Override
    public void remove(int id) {

        inMemoryHistoryManager.remove(id);
    }

    @Override
    public List<Task> getHistory() {

        return inMemoryHistoryManager.getTasks();
    }
}

class CustomLinkedList {

    public Node<Task> head = null;//задаем нул, чтобы не писать метод .isEmpty()
    public Node<Task> tail = null;

    public Map<Integer, Node<Task>> nodeMap = new HashMap<>();//храним ноды в порядке добавления(при первоначальных
    // тестах результат выдавался отсортированным по id, поэтому была выбрана LinkedHashMap).

    public void linkLast(Task task) {

        Node<Task> newNode = new Node<>(task, null, null);

        if (this.head == null) {//если лист пуст - пишем начало и конец

            this.head = newNode;
            this.tail = this.head;
        } else {

            newNode.prev = this.tail;
            this.tail.next = newNode;
            this.tail = newNode;

            Node<Task> delNode = nodeMap.getOrDefault(task.getTaskId(), null);// ищем повторы в мапе
            // т.к. мы завязаны на скорости просмотра, то не гоняем .containsKey(), а сразу пишем нод из мапы,
            // если он там существует.
            if (delNode != null) {//если нод есть в мапе, то стираем старый
                removeNode(delNode);
            }

        }

        nodeMap.put(task.getTaskId(), newNode);//новый нод мы всегда пишем в мапу
    }

    public List<Task> getTasks() {
        List<Task> history = new ArrayList<>();
        Node<Task> read = this.head;//начинаем с головы, ибо точно знаем, что он первый в списке.

        if (this.head.next == null) {//если в списке только 1 нод, у него нет некста
            history.add(read.data);
        } else {
            while (read.next != null) {//цикл while не написал бы последний нод
                history.add(read.data);
                read = read.next;
            }
            history.add(read.data);//тут пишем последний нод
        }

        return history;
    }

    public void remove(int id) {

        removeNode(nodeMap.get(id));
        nodeMap.remove(id);

    }

    public void removeNode(Node<Task> node) {//удаляем нод с перезаписью ссылок UPD: комментарии учтены, но nodeMap
        // трогать надо, чтобы не было дубликатов нодов в мапе, все равно базовые операции выполняются за 0(1).

        Node<Task> prevNode;//нужно перезаписать ссылки на предыдущий и следующий ноды для соседей
        Node<Task> nextNode;

        if (node.prev != null) {
            prevNode = node.prev;
        } else {
            prevNode = null;
        }
        if (node.next != null) {
            nextNode = node.next;
        } else {
            nextNode = null;
        }

        if (prevNode == null) {//если нет предыдущего нода — пишем только следующий и он становится head

            nextNode.prev = null;//перезаписываем ссылку
            this.head = nextNode;

            nodeMap.remove(nextNode.data.getTaskId());//перезаписываем обновлённый нод в мапу
            nodeMap.put(nextNode.data.getTaskId(), nextNode);
        } else if (nextNode == null) {//если нет следующего нода — пишем только предыдущий

            prevNode.next = null;

            nodeMap.remove(prevNode.data.getTaskId());
            nodeMap.put(prevNode.data.getTaskId(), prevNode);
        } else {//если нод в середине списка — переписываем все

            prevNode.next = nextNode;
            nextNode.prev = prevNode;

            nodeMap.remove(prevNode.data.getTaskId());
            nodeMap.remove(nextNode.data.getTaskId());
            nodeMap.put(prevNode.data.getTaskId(), prevNode);
            nodeMap.put(nextNode.data.getTaskId(), nextNode);
        }
        nodeMap.remove(node.data.getTaskId());//удаляем ненужный нод
    }
}
