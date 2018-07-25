package com.epam.stack;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingStack<E> {

    private final Object[] data;
    private final Lock lock;
    private final Condition stackEmpty;
    private final Condition stackFull;

    private int size;
    private int index = 0;

    public BlockingStack(int size) {
        this(size, false);
    }

    public BlockingStack(int size, boolean fair) {
        assert(size > 0);
        data = new Object[size];
        this.size = size;
        lock = new ReentrantLock(fair);
        stackEmpty = lock.newCondition();
        stackFull = lock.newCondition();
    }

    public void push(E element) throws InterruptedException {
        lock.lock();
        try {
            if (index < size) {
                data[index++] = element;
            }
            else {
                stackFull.await();
            }
        } finally {
            lock.unlock();
        }
    }

    public E pop() throws InterruptedException {
        lock.lock();
        try {
            if (index == 0) {
                stackEmpty.await();
            }
            return (E) data[index--];
        } finally {
            lock.unlock();
        }
    }

    public E peek() {
        lock.lock();
        try {
            if (index >= 0 && index < size) {
                return (E) data[index];
            }
            else {
                return null;
            }
        } finally {
            lock.unlock();
        }
    }

}
