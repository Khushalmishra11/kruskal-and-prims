import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import javax.swing.*;

class Edge {
    int src, dest, weight;

    public Edge(int src, int dest, int weight) {
        this.src = src;
        this.dest = dest;
        this.weight = weight;
    }
}

class Graph {
    int vertices;
    ArrayList<Edge> edges = new ArrayList<>();

    public Graph(int vertices) {
        this.vertices = vertices;
    }

    public void addEdge(int src, int dest, int weight) {
        edges.add(new Edge(src, dest, weight));
    }
}

public class MSTApp extends JFrame {
    private Graph graph;
    private JTextArea resultArea;
    private JComboBox<String> algorithmChoice;

    public MSTApp() {
        setTitle("Kruskal's and Prim's Algorithm MST");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        graph = new Graph(9); // 9 vertices from 'a' to 'i'

        // Add edges manually as per the image
        addGraphEdges();

        // User Interface
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Dropdown for algorithm choice
        String[] algorithms = {"Kruskal's Algorithm", "Prim's Algorithm"};
        algorithmChoice = new JComboBox<>(algorithms);
        panel.add(algorithmChoice, BorderLayout.NORTH);

        // Result Area
        resultArea = new JTextArea(15, 40);
        resultArea.setEditable(false);
        panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        // Compute Button
        JButton computeButton = new JButton("Compute MST");
        computeButton.addActionListener(new ComputeMSTListener());
        panel.add(computeButton, BorderLayout.SOUTH);

        add(panel);
    }

    private void addGraphEdges() {
        // Add edges as per the graph image (indices are 0-based for 'a' to 'i')
        graph.addEdge(0, 1, 4); // a - b, weight 4
        graph.addEdge(0, 7, 8); // a - h, weight 8
        graph.addEdge(1, 2, 8); // b - c, weight 8
        graph.addEdge(1, 7, 11); // b - h, weight 11
        graph.addEdge(2, 3, 7); // c - d, weight 7
        graph.addEdge(2, 5, 4); // c - f, weight 4
        graph.addEdge(2, 8, 2); // c - i, weight 2
        graph.addEdge(3, 4, 9); // d - e, weight 9
        graph.addEdge(3, 5, 14); // d - f, weight 14
        graph.addEdge(4, 5, 10); // e - f, weight 10
        graph.addEdge(5, 6, 2); // f - g, weight 2
        graph.addEdge(6, 7, 1); // g - h, weight 1
        graph.addEdge(6, 8, 6); // g - i, weight 6
        graph.addEdge(7, 8, 7); // h - i, weight 7
    }

    private class ComputeMSTListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedAlgorithm = (String) algorithmChoice.getSelectedItem();
            if (selectedAlgorithm.equals("Kruskal's Algorithm")) {
                resultArea.setText("Kruskal's MST:\n" + kruskalMST(graph));
            } else {
                resultArea.setText("Prim's MST:\n" + primMST(graph));
            }
        }
    }

    private String kruskalMST(Graph graph) {
        ArrayList<Edge> result = new ArrayList<>();
        int[] parent = new int[graph.vertices];
        Arrays.fill(parent, -1);

        graph.edges.sort(Comparator.comparingInt(edge -> edge.weight));

        for (Edge edge : graph.edges) {
            int srcRoot = find(parent, edge.src);
            int destRoot = find(parent, edge.dest);

            if (srcRoot != destRoot) {
                result.add(edge);
                union(parent, srcRoot, destRoot);
            }
        }

        return formatResult(result);
    }

    private String primMST(Graph graph) {
        ArrayList<Edge> result = new ArrayList<>();
        boolean[] inMST = new boolean[graph.vertices];
        int[] key = new int[graph.vertices];
        Arrays.fill(key, Integer.MAX_VALUE);
        key[0] = 0; // Start from node 'a' (index 0)
        int[] parent = new int[graph.vertices];
        Arrays.fill(parent, -1);

        for (int count = 0; count < graph.vertices - 1; count++) {
            int u = minKeyVertex(key, inMST);
            inMST[u] = true;

            for (Edge edge : graph.edges) {
                if ((edge.src == u || edge.dest == u) && !inMST[edge.dest]) {
                    int v = (edge.src == u) ? edge.dest : edge.src;
                    if (edge.weight < key[v]) {
                        key[v] = edge.weight;
                        parent[v] = u;
                    }
                }
            }
        }

        for (int i = 1; i < graph.vertices; i++) {
            if (parent[i] != -1) {
                result.add(new Edge(parent[i], i, key[i]));
            }
        }

        return formatResult(result);
    }

    private int minKeyVertex(int[] key, boolean[] inMST) {
        int min = Integer.MAX_VALUE, minIndex = -1;
        for (int v = 0; v < key.length; v++) {
            if (!inMST[v] && key[v] < min) {
                min = key[v];
                minIndex = v;
            }
        }
        return minIndex;
    }

    private int find(int[] parent, int vertex) {
        if (parent[vertex] == -1)
            return vertex;
        return find(parent, parent[vertex]);
    }

    private void union(int[] parent, int srcRoot, int destRoot) {
        parent[srcRoot] = destRoot;
    }

    private String formatResult(ArrayList<Edge> edges) {
        StringBuilder sb = new StringBuilder();
        int totalWeight = 0;
        for (Edge edge : edges) {
            char src = (char) ('a' + edge.src);
            char dest = (char) ('a' + edge.dest);
            sb.append(src).append(" - ").append(dest).append(" : ").append(edge.weight).append("\n");
            totalWeight += edge.weight;
        }
        sb.append("Total Weight: ").append(totalWeight);
        return sb.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MSTApp app = new MSTApp();
            app.setVisible(true);
        });
    }
}
