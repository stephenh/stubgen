package com.foo;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class StubListBase<E> implements List<E> {

    @Override
    public boolean remove(Object arg0) {
        return false;
    }

    @Override
    public E remove(int arg0) {
        return null;
    }

    @Override
    public E set(int arg0, E arg1) {
        return null;
    }

    @Override
    public boolean containsAll(Collection<?> arg0) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
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
    public void add(int arg0, E arg1) {
    }

    @Override
    public boolean add(E arg0) {
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
    public List<E> subList(int arg0, int arg1) {
        return null;
    }

    @Override
    public boolean removeAll(Collection<?> arg0) {
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return null;
    }

    @Override
    public <T> T[] toArray(T[] arg0) {
        return null;
    }

    @Override
    public boolean addAll(int arg0, Collection<? extends E> arg1) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> arg0) {
        return false;
    }

    @Override
    public E get(int arg0) {
        return null;
    }

    @Override
    public boolean equals(Object arg0) {
        return false;
    }

    @Override
    public ListIterator<E> listIterator() {
        return null;
    }

    @Override
    public ListIterator<E> listIterator(int arg0) {
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
