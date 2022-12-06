package com.pigeoff.metro.utils;

import com.pigeoff.metro.data.Station;

import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Route {
    private static final int INFINITY = 9999;
    private static final String[] metros = new String[]{"1","2","3","4","5","6","7","8","9","10","11","12","13","14","3B","7B"};
    private static final String[] trams = new String[]{"T1","T2","T3b","T3a","T4","T5","T6","T7","T8","T9","T11","T13"};

    private final List<Station> network;

    public Route(ArrayList<Station> ntw) {
        network = ntw;
    }

    private int getStationIndex(String id) {
        int i = 0;
        for (Station s : network) {
            if (id.equals(s.id)) return i;
            else i++;
        }
        return -1;
    }

    private Station getStationFrom(String id) {
        for (Station s : network) {
            if (id.equals(s.id)) return s;
        }
        return null;
    }

    private Station getStationFromStr(String str) {
        str = Utils.reduceStr(str);
        int maxProx = 0;
        Station maxStation = null;
        for (Station s : network) {
            String minName = Utils.reduceStr(s.name);
            LevenshteinDistance ld = new LevenshteinDistance();
            int prox = ld.apply(str, minName);
            if (prox > maxProx && prox > 3) {
                maxProx = prox;
                maxStation = s;
            }
        }
        return maxStation;
    }

    public ArrayList<Station> shortestWay(String from, String to) throws Exception {
        Station sfrom = getStationFrom(from);
        Station sto = getStationFrom(to);
        return shortestWay(sfrom, sto);
    }

    public ArrayList<Station> shortestWay(Station from, Station to) throws Exception {
        if (from == null || to == null) throw new Exception("Il manque une ou plusieurs stations.");

        ArrayList<Integer> dist = Utils.initiateList(network.size(), INFINITY);
        ArrayList<String> visited = new ArrayList<>();
        ArrayList<Integer> parents = Utils.initiateList(network.size(), -1);
        ArrayList<String> lignes = Utils.initiateList(network.size(), "");

        dist.set(getStationIndex(from.id), 0);

        for (int i = 0; i < network.size(); ++i) {
            int uv = INFINITY;
            int u = -1;
            for (int j = 0; j < network.size(); ++j) {
                if (dist.get(j) < uv
                    && dist.get(j) < INFINITY
                    && !visited.contains(network.get(j).id)) {
                    uv = dist.get(j);
                    u = j;
                }
            }

            if (u >= 0) {
                Station s = network.get(u);
                visited.add(s.id);
                for (int a = 0; a < s.next.size(); ++a) {
                    int dist_u = (i == 0) ? 1 : s.next.get(a).weight;
                    int next_id = getStationIndex(s.next.get(a).id);

                    if (!visited.contains(network.get(next_id).id)) {
                        if (dist.get(u) + dist_u < dist.get(next_id)) {
                            dist.set(next_id, dist.get(u) + dist_u);
                            parents.set(next_id, u);
                            lignes.set(next_id, s.next.get(a).line);
                        }
                    }
                }
            }
        }

        int k = 0;
        int ffrom = getStationIndex(from.id);
        int fto = getStationIndex(to.id);
        ArrayList<Station> parcours = new ArrayList<>();
        parcours.add(to);

        while (fto != ffrom && k < network.size()) {
            parcours.add(network.get(parents.get(fto)));
            fto = parents.get(fto);
            k++;
        }

        Collections.reverse(parcours);
        if (parcours.size() > 1) {
            for (int i = 0; i < parcours.size() - 1; ++i) {
                Station st = parcours.get(i);
                Station st1 = parcours.get(i + 1);
                String pid = st.parent;
                String l = st.line;
                String l1 = st1.line;

                // This condition could be replaced by
                // if (i == 0 || !l.equals(i, nst)
                if (i == 0) {
                    Station nst = getStationFrom(pid + ":" + l1);
                    parcours.set(i, nst);
                } else if (!l.equals(l1)) {
                    parcours.set(i, getStationFrom(pid + ":" + l1));
                }
            }
        }

        return parcours;
    }
}
