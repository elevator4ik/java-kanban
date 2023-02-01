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
    public void clear() {

        inMemoryHistoryManager.clear();
    }

    @Override
    public List<Task> getHistory() {

        return inMemoryHistoryManager.getTasks();
    }
}

class CustomLinkedList {

    public Node<Task> head = null;//задаем нул, чтобы не писать метод .isEmpty()
    public Node<Task> tail = null;

    public Map<Integer, Node<Task>> nodeMap = new HashMap<>();

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

        if (this.head != null) {//проверяем, есть ли начало у списка, если нет - возвращаем null
            if (this.head.next == null) {//если в списке только 1 нод, у него нет некста
                history.add(read.data);
            } else {
                do {
                    history.add(read.data);
                    read = read.next;
                }
                while (read.next != null);//отловил баг - раньше не читался последний нод, т.е. тэйл
                read = this.tail;
                history.add(read.data);
            }
        }
        return history;
    }

    public void remove(int id) {

        removeNode(nodeMap.get(id));
        nodeMap.remove(id);

    }

    public void clear() {

        nodeMap.clear();
        this.head = null;
        this.tail = null;

    }

    public void removeNode(Node<Task> node) {//удаляем нод с перезаписью ссылок

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
        if (prevNode == null & nextNode != null) {//если нет предыдущего нода — пишем только следующий

            nextNode.prev = null;//перезаписываем ссылку
            this.head = nextNode;//он становится head

        } else if (nextNode == null & prevNode != null) {//если нет следующего нода — пишем только предыдущий

            prevNode.next = null;
            this.tail = prevNode;

        } else if (prevNode == null & nextNode == null) {//если нет ссылок и удаляем последний нод - чистим head и tail

            this.head = null;
            this.tail = null;
        } else {//если нод в середине списка — переписываем все

            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        }
        nodeMap.remove(node.data.getTaskId());//удаляем ненужный нод
    }
}
