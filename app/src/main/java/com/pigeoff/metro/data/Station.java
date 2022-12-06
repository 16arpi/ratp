package com.pigeoff.metro.data;

import java.util.List;

public class Station {
    public class Next {
        public String line;
        public String id;
        public int weight;
    }

    public class Sorties {
        public int nb;
        public String name;
        public Float lat;
        public Float lon;
    }

    public String id;
    public String parent;
    public String name;
    public Float lat;
    public Float lon;
    public String line;
    public String color;
    public String textColor;
    public List<Sorties> sorties;
    public List<Next> next;

}
