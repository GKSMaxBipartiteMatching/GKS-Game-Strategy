import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.io.PrintWriter;
import java.util.Collections;

public class MaxBipartiteMatching {
    public static int[] matching = new int[1365];
    public static int[] codewords = new int[2048];

    // Generates a code of given size with a distance of correctionBits
    public static int generateMDSCodeSize(int size, int correctionBits) {
        int[] code = new int[(int) Math.pow(2, size - correctionBits)];
        int added=0;
        int[] remainingBitVectors = new int[(int) Math.pow(2, size) + 1];

        for (int bitVect = 0; bitVect < (int) Math.pow(2, size); bitVect++) {
            remainingBitVectors[bitVect] = bitVect;
        }

        remainingBitVectors[(int) Math.pow(2, size)] = -1;
        int newRemainingBitVectorsInd = remainingBitVectors.length;

        while (remainingBitVectors[0] != -1) {
            code[added] = remainingBitVectors[0];
            added++;
            int[] newRemainingBitVectors = new int[newRemainingBitVectorsInd];
            int remainingBitVectorsInd = 1;
            newRemainingBitVectorsInd = 0;

            while (remainingBitVectors[remainingBitVectorsInd] != -1) {
                if (!(numberOnes(remainingBitVectors[remainingBitVectorsInd] ^ code[added - 1]) < correctionBits))
                {
                    newRemainingBitVectors[newRemainingBitVectorsInd] = remainingBitVectors[remainingBitVectorsInd];
                    newRemainingBitVectorsInd++;
                }

                remainingBitVectorsInd++;
            }

            newRemainingBitVectors[newRemainingBitVectorsInd] = -1;
            newRemainingBitVectorsInd++;
            remainingBitVectors = newRemainingBitVectors;
        }

        for (int i = 0; i < 2048; i++) {
            codewords[i] = code[i];
        }

        return added;
    }

    // Counts the number of ones in an integer's binary representation
    public static int numberOnes(int bits)
    {
        int numberOnes = 0;

        for(int i = 0; i < 32; i++)
        {
            numberOnes += (bits >> i) & 1;
        }
        return numberOnes;
    }

    // Returns all possible subsets of a given set
    public static void getSubsets(ArrayList<Integer> superSet, int k, int idx,
            HashSet<Integer> current, ArrayList<HashSet<Integer>> solution) {
        if (current.size() == k) {
            solution.add(new HashSet<>(current));
            return;
        }
        if (idx == superSet.size()) {
            return;
        }

        Integer x = superSet.get(idx);
        current.add(x);
        getSubsets(superSet, k, idx + 1, current, solution);
        current.remove(x);
        getSubsets(superSet, k, idx + 1, current, solution);
    }

    // Helper method for getSubsets
    public static ArrayList<HashSet<Integer>> getSubsets(ArrayList<Integer> superSet, int k) {
        ArrayList<HashSet<Integer>> res = new ArrayList<>();
        getSubsets(superSet, k, 0, new HashSet<Integer>(), res);
        return res;
    }

    // Determines of a matching for a vertex u is possible
    public static boolean bpm(boolean bpGraph[][], int u, boolean seen[],
            int matchR[], int numvert) {
        for (int v = 0; v < numvert; v++)
        {
            if (bpGraph[u][v] && !seen[v])
            {
                seen[v] = true;

                if (matchR[v] < 0 || bpm(bpGraph, matchR[v],
                        seen, matchR, numvert)) {
                    matchR[v] = u;
                    return true;
                }
            }
        }
        return false;
    }

    // Returns the size of the max bipartite matching from M to N
    public static int maxBPM(boolean bpGraph[][], int numvert)
    {
        int matchR[] = new int[numvert];
 
        for(int i = 0; i < numvert; i++)
            matchR[i] = -1;
 
        int result = 0;

        for (int u = 0; u < numvert; u++)
        {
            boolean seen[] = new boolean[numvert];

            for(int i = 0; i < numvert; i++) {
                seen[i] = false;
            }
            if (bpm(bpGraph, u, seen, matchR, numvert)) {
                result++;
            }
        }
        for (int i = 0; i < 1365; i++) {
            matching[i] = matchR[i];
        }

        return result;
    }

    // Determines whether an integer has bit at index num in its binary representation
    public static boolean hasBitAt(int bit, int num, HashSet<Integer> subset) {
        for (int i : subset) {
            if (((num >> (i - 1)) & 1) != bit) {
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        generateMDSCodeSize(15, 3);
        ArrayList<Integer> list = new ArrayList<Integer>();
        ArrayList<Integer> indices = new ArrayList<Integer>();

        for (int i = 0; i < codewords.length; i++) {
            if (numberOnes(codewords[i]) >= 4) {
                list.add(codewords[i]);
            }
        }

        for (int i = 1; i <= 15; i++) {
            indices.add(i);
        }

        List<HashSet<Integer>> subsets = new ArrayList<HashSet<Integer>>();
        subsets = getSubsets(indices, 4);
        boolean[][] bpgraph = new boolean[list.size()][list.size()];

        for (int i = 0; i < bpgraph.length; i++) {
            for (int k = 0; k < bpgraph.length; k++) {
                if (k >= subsets.size()) {
                    bpgraph[i][k] = false;
                } else if (hasBitAt(1, list.get(i), subsets.get(k))) {
                    bpgraph[i][k] = true;
                } else {
                    bpgraph[i][k] = false;
                }
            }
        }

        maxBPM(bpgraph, list.size());
    }
}