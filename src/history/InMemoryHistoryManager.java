package history;

import modul.Node;
import modul.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    CustomLinkedList inMemoryHistoryManager = new CustomLinkedList();

    public void printer() {
        System.out.println(inMemoryHistoryManager.nodeMap + "\n|\n");
    }

    @Override
    public void add(Task task) {

        inMemoryHistoryManager.linkLast(task);

    }

    @Override
    public void remove(int id) {

        Node<Task> node = inMemoryHistoryManager.getNodeById(id);
        inMemoryHistoryManager.removeNode(node);
    }

    @Override
    public List<Task> getHistory() {

        return inMemoryHistoryManager.getTasks();
    }
}

class CustomLinkedList extends LinkedList {

    public Node<Task> head = null;//задаем нул, чтобы не писать метод .isEmpty()
    public Node<Task> tail = null;
    private int size = 0;

    public Map<Integer, Node<Task>> nodeMap = new LinkedHashMap<>();//храним ноды в порядке добавления, обычная мапа
    // сортирует по ключу, а оно нам не надо

    @Override
    public int size() {//если не переписать, то всегда дает 0

        return size;

    }

    public void linkLast(Task task) {

        Node<Task> newNode = new Node<>(task, null, null);

        if (this.head == null) {//если лист пуст - пишем начало и конец

            this.head = newNode;
            this.tail = this.head;
            size++;
        } else {

            newNode.prev = this.tail;
            this.tail.next = newNode;
            this.tail = newNode;
            size++;

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

        if (prevNode == null) {//если нет предыдущего нода — пишем только следующий и он становится head

            int nextNodeId = getNodeId(nextNode);//ищем id нода, чтоб перезаписать обновлённый в мапу
            nextNode.prev = null;//перезаписываем ссылку
            this.head = nextNode;

            nodeMap.remove(nextNodeId);//перезаписываем обновлённый нод в мапу
            nodeMap.put(nextNodeId, nextNode);
        } else if (nextNode == null) {//если нет следующего нода — пишем только предыдущий

            int prevNodeId = getNodeId(prevNode);
            prevNode.next = null;

            nodeMap.remove(prevNodeId);
            nodeMap.put(prevNodeId, prevNode);
        } else {//если нод в середине списка — переписываем все

            int prevNodeId = getNodeId(prevNode);
            int nextNodeId = getNodeId(nextNode);
            prevNode.next = nextNode;
            nextNode.prev = prevNode;

            nodeMap.remove(prevNodeId);
            nodeMap.remove(nextNodeId);
            nodeMap.put(prevNodeId, prevNode);
            nodeMap.put(nextNodeId, nextNode);
        }
        int nodeId = getNodeId(node);
        nodeMap.remove(nodeId);//удаляем ненужный нод
        size--;
    }

    public int getNodeId(Node<Task> node) { //ищем id нода для перезаписи, вынес в отдельный метод, чтоб не дублировать код.

        Set<Map.Entry<Integer, Node<Task>>> entrySet = nodeMap.entrySet();
        int i = 0; //переменная для ключа

        for (Map.Entry<Integer, Node<Task>> pair : entrySet) {
            if (node.equals(pair.getValue())) {
                i = pair.getKey();// нашли наше значение и пишем ключ
            }
        }
        return i; //возвращаем id
    }

    public Node<Task> getNodeById(int id) {

        return nodeMap.get(id);
    }
}
