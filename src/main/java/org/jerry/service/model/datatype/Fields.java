package org.jerry.service.model.datatype;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Fields implements Iterable<String>, Serializable {
    private List<String> _fields;
    private Map<String, Integer> _index;

    public Fields(String... fields) {
        this((List)Arrays.asList(fields));
    }

    public Fields(List<String> fields) {
        this._index = new HashMap();
        this._fields = new ArrayList(fields.size());
        Iterator i$ = fields.iterator();

        while(i$.hasNext()) {
            String field = (String)i$.next();
            if(this._fields.contains(field)) {
                throw new IllegalArgumentException(String.format("duplicate field \'%s\'", new Object[]{field}));
            }

            this._fields.add(field);
        }

        this.index();
    }

    public List<Object> select(Fields selector, List<Object> tuple) {
        ArrayList ret = new ArrayList(selector.size());
        Iterator i$ = selector.iterator();

        while(i$.hasNext()) {
            String s = (String)i$.next();
            ret.add(tuple.get(((Integer)this._index.get(s)).intValue()));
        }

        return ret;
    }

    public List<String> toList() {
        return new ArrayList(this._fields);
    }

    public int size() {
        return this._fields.size();
    }

    public String get(int index) {
        return (String)this._fields.get(index);
    }

    public Iterator<String> iterator() {
        return this._fields.iterator();
    }

    public int fieldIndex(String field) {
        Integer ret = (Integer)this._index.get(field);
        if(ret == null) {
            throw new IllegalArgumentException(field + " does not exist");
        } else {
            return ret.intValue();
        }
    }

    public boolean contains(String field) {
        return this._index.containsKey(field);
    }

    private void index() {
        for(int i = 0; i < this._fields.size(); ++i) {
            this._index.put(this._fields.get(i), Integer.valueOf(i));
        }

    }

    public String toString() {
        return this._fields.toString();
    }
}
