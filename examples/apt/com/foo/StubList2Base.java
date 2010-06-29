package com.foo;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class StubList2Base implements List<String> {

    @Override
    public boolean remove(Object arg0) {
        return false;
    }

    @Override
    public String remove(int arg0) {
        return null;
    }

    @Override
    public String set(int arg0, String arg1) {
        return null;
    }

    @Override
    public boolean containsAll(Collection<?> arg0) {
        return false;
    }

    @Override
    public boolean contains(Object arg0) {
        return false;
    }

    @Override
    public int indexOf(Object arg0) {
        return 0;
    }

    @Override
    public void add(int arg0, String arg1) {
    }

    @Override
    public boolean add(String arg0) {
        return false;
    }

    @Override
    public void clear() {
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> arg0) {
        return false;
    }

    @Override
    public List<String> subList(int arg0, int arg1) {
        return null;
    }

    @Override
    public boolean removeAll(Collection<?> arg0) {
        return false;
    }

    @Override
    public Iterator<String> iterator() {
        return null;
    }

    @Override
    public <T> T[] toArray(T[] arg0) {
        return null;
    }

    @Override
    public Object[] toArray() {
        return null;
    }

    @Override
    public boolean addAll(int arg0, Collection<? extends String> arg1) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends String> arg0) {
        return false;
    }

    @Override
    public String get(int arg0) {
        return null;
    }

    @Override
    public ListIterator<String> listIterator() {
        return null;
    }

    @Override
    public ListIterator<String> listIterator(int arg0) {
        return null;
    }

    @Override
    public int lastIndexOf(Object arg0) {
        return 0;
    }

    @Override
    public int size() {
        return 0;
    }

}
