import java.util.*;

class Adventurer {
    int id;
    int points;

    Adventurer(int id, int points) {
        this.id = id;
        this.points = points;
    }
}

class Zone {
    int id;
    int numAdventurers;
    Adventurer[] adventurers;
    int totalPoints;
    BinaryTree tree;

    Zone(int id, int numAdventurers) {
        this.id = id;
        this.numAdventurers = numAdventurers;
        this.adventurers = new Adventurer[numAdventurers];
        this.totalPoints = 0;
        this.tree = new BinaryTree();
    }

    void addAdventurer(int id, int points) {
        Adventurer a = new Adventurer(id, points);
        tree.insert(a);
        adventurers[id - 1] = a;
        totalPoints += points;
    }

    void updatePoints(int id, int points) {
        Adventurer a = adventurers[id - 1];
        if (a != null) {
            totalPoints -= a.points;
            a.points += points;
            totalPoints += a.points;
            tree.update(a);
        }
    }

    void removeAdventurer(int id) {
        Adventurer a = adventurers[id - 1];
        if (a != null) {
            totalPoints -= a.points;
            tree.remove(a);
            adventurers[id - 1] = null;
            numAdventurers--; // Perbarui jumlah petualang
        }
    }

     int getAveragePoints() {
        if (numAdventurers == 0) return 0; // Cek pembagian dengan nol
        return totalPoints / numAdventurers;
}

class Guild {
    CircularDoublyLinkedList<Zone> zones;
    int currentZoneIndex;
    private static int nextAdventurerId = 1; // Penghitung ID global

    Guild() {
        zones = new CircularDoublyLinkedList<>();
        currentZoneIndex = 0;
    }

    void addZone(int numAdventurers) {
        Zone z = new Zone(zones.size() + 1, numAdventurers);
        zones.addLast(z);
    }

    void moveLeft() {
        if (currentZoneIndex > 0) {
            currentZoneIndex--;
        }
    }

    void moveRight() {
        if (currentZoneIndex < zones.size() - 1) {
            currentZoneIndex++;
        }
    }

    Zone getCurrentZone() {
        return zones.get(currentZoneIndex);
    }

    void sortZones() {
        Collections.sort(zones.toList(), (a, b) -> {
            int aAvg = a.getAveragePoints();
            int bAvg = b.getAveragePoints();
            if (aAvg != bAvg) {
                return bAvg - aAvg;
            } else {
                return a.id - b.id;
            }
        });
    }
    void handleZoneRemoval() {
        if (getCurrentZone().tree.root == null) {
            zones.remove(currentZoneIndex);
            if (currentZoneIndex >= zones.size()) { // Pastikan indeks valid
                currentZoneIndex = 0;
            }
        }
    }
    void addAdventurerToCurrentZone(int points) {
        Zone currentZone = getCurrentZone();
        currentZone.addAdventurer(nextAdventurerId++, points);
    }
}

class BinaryTree {
    Node root;

    class Node {
        Adventurer adventurer;
        Node left, right;

        Node(Adventurer a) {
            this.adventurer = a;
            left = right = null;
        }
    }

    void insert(Adventurer a) {
        root = insertRecursive(root, a);
    }

    Node insertRecursive(Node node, Adventurer a) {
        if (node == null) {
            return new Node(a);
        }

        if (a.points < node.adventurer.points || (a.points == node.adventurer.points && a.id < node.adventurer.id)) {
            node.left = insertRecursive(node.left, a);
        } else {
            node.right = insertRecursive(node.right, a);
        }

        return node;
    }

    void update(Adventurer a) {
        updateRecursive(root, a);
    }

    void updateRecursive(Node node, Adventurer a) {
        if (node == null) {
            return;
        }

        if (a.id == node.adventurer.id) {
            node.adventurer = a;
        } else if (a.points < node.adventurer.points || (a.points == node.adventurer.points && a.id < node.adventurer.id)) {
            updateRecursive(node.left, a);
        } else {
            updateRecursive(node.right, a);
        }
    }

    void remove(Adventurer a) {
        root = removeRecursive(root, a);
    }

    Node removeRecursive(Node node, Adventurer a) {
        if (node == null) {
            return null;
        }

        if (a.id == node.adventurer.id) {
            if (node.left == null) {
                return node.right;
            } else if (node.right == null) {
                return node.left;
            } else {
                node.adventurer = findMin(node.right).adventurer;
                node.right = removeRecursive(node.right, node.adventurer);
            }
        } else if (a.points < node.adventurer.points || (a.points == node.adventurer.points && a.id < node.adventurer.id)) {
            node.left = removeRecursive(node.left, a);
        } else {
            node.right = removeRecursive(node.right, a);
        }

        return node;
    }

    Node findMin(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }
}

class CircularDoublyLinkedList<T> {
    private class Node {
        Node(T data) {
            this.data = data;
            this.prev = this.next = null;
        }
    }

    private Node head;
    private int size;

    CircularDoublyLinkedList() {
        this.head = null;
        this.size = 0;
    }

    void addLast(T data) {
        Node newNode = new Node(data);
        if (head == null) {
            head = newNode;
            newNode.prev = newNode;
            newNode.next = newNode;
        } else {
            Node last = head.prev;
            last.next = newNode;
            newNode.prev = last;
            newNode.next = head;
            head.prev = newNode;
        }
        size++;
    }

    void remove(int index) {
        if (index < 0 || index >= size || head == null) return;
        if (size == 1) {
            head = null;
        } else {
            Node curr = head;
            for (int i = 0; i < index; i++) {
                curr = curr.next;
            }
            Node prev = curr.prev;
            Node next = curr.next;
            prev.next = next;
            next.prev = prev;
            if (curr == head) {
                head = next;
            }
        }
        size--;
        if (size == 0) head = null; // Pastikan head null jika daftar kosong
    }

    private Node head;
    private int size;

    CircularDoublyLinkedList() {
        this.head = null;
        this.size = 0;
    }

    void addLast(T data) {
        Node newNode = new Node(data);
        if (head == null) {
            head = newNode;
            newNode.prev = newNode;
            newNode.next = newNode;
        } else {
            Node last = head.prev;
            last.next = newNode;
            newNode.prev = last;
            newNode.next = head;
            head.prev = newNode;
        }
        size++;
    }

    void remove(int index) {
        if (index < 0 || index >= size) {
            return;
        }
        if (size == 1) {
            head = null;
        } else {
            Node curr = head;
            for (int i = 0; i < index; i++) {
                curr = curr.next;
            }
            Node prev = curr.prev;
            Node next = curr.next;
            prev.next = next;
            next.prev = prev;
            if (curr == head) {
                head = next;
            }
        }
        size--;
    }

    T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        Node curr = head;
        for (int i = 0; i < index; i++) {
            curr = curr.next;
        }
        return curr.data;
    }

    List<T> toList() {
        List<T> list = new ArrayList<>();
        if (head != null) {
            Node curr = head;
            do {
                list.add(curr.data);
                curr = curr.next;
            } while (curr != head);
        }
        return list;
    }

    int size() {
        return size;
    }
}

public class GuildPointManagementSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int M = Integer.parseInt(sc.nextLine());
        Guild guild = new Guild();
    
        for (int i = 0; i < M; i++) {
            int numAdventurers = Integer.parseInt(sc.nextLine());
            guild.addZone(numAdventurers);
            String[] pointsStr = sc.nextLine().split(" ");
            for (int j = 0; j < numAdventurers; j++) {
                int points = Integer.parseInt(pointsStr[j]);
                guild.getCurrentZone().addAdventurer(j + 1, points);
            }
        }
    
        int Q = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < Q; i++) {
            String query = sc.nextLine();
            String[] queryParts = query.split(" ");
            switch (queryParts[0]) {
                case "T":
                    int id = Integer.parseInt(queryParts[1]);
                    int points = Integer.parseInt(queryParts[2]);
                    boolean found = false;
                    for (Adventurer a : guild.getCurrentZone().adventurers) {
                        if (a.id == id) {
                            guild.getCurrentZone().updatePoints(id, points);
                            System.out.println(a.points);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        System.out.println(-1);
                    }
                    break;
                case "A":
                    points = sc.nextInt();
                    int newId = guild.getCurrentZone().tree.root.adventurer.id + 1; // Potensi duplikasi ID
                    guild.getCurrentZone().addAdventurer(newId, points);
                    System.out.println(newId);
                    break;
                case "P":
                    char direction = sc.next().charAt(0);
                    if (direction == 'L') {
                        guild.moveLeft();
                    } else {
                        guild.moveRight();
                    }
                    System.out.println(guild.getCurrentZone().id);
                    break;
                case "K":
                    int N = sc.nextInt();
                    guild.addZone(N);
                    System.out.println(guild.zones.size());
                    break;
                case "S":
                    guild.sortZones();
                    List<Zone> sortedZones = guild.zones.toList();
                    System.out.println(sortedZones.get(0).id + " " + sortedZones.get(sortedZones.size() - 1).id);
                    break;
                case "E":
                    int threshold = sc.nextInt();
                    int removedCount = 0;
                    while (guild.getCurrentZone().tree.root != null && guild.getCurrentZone().tree.root.adventurer.points < threshold) {
                        guild.getCurrentZone().removeAdventurer(guild.getCurrentZone().tree.root.adventurer.id);
                        removedCount++;
                    }
                    if (guild.zones.size() == 1 && guild.getCurrentZone().tree.root == null) {
                        System.out.println(-1);
                    } else if (guild.getCurrentZone().tree.root == null) {
                        guild.zones.remove(guild.currentZoneIndex);
                        guild.moveRight();
                        System.out.println(removedCount);
                    } else {
                        System.out.println(removedCount);
                    }
                    break;
                case "R":
                    if (guild.zones.size() == 1) {
                        System.out.println(-1);
                    } else {
                        Zone currentZone = guild.getCurrentZone();
                        Zone nextZone = guild.zones.get((guild.currentZoneIndex + 1) % guild.zones.size());
                        Adventurer lowestInCurrentZone = currentZone.tree.root.adventurer;
                        Adventurer highestInNextZone = nextZone.tree.root.adventurer;
                        if (lowestInCurrentZone.points < highestInNextZone.points) {
                            currentZone.removeAdventurer(lowestInCurrentZone.id);
                            nextZone.addAdventurer(lowestInCurrentZone.id, lowestInCurrentZone.points);
                            System.out.println(lowestInCurrentZone.id);
                        } else {
                            System.out.println(-1);
                        } 
                    }
                    break;
            }
        }
    }
}